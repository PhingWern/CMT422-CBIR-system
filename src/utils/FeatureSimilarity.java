package utils;

import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.LireFeature;
import net.semanticmetadata.lire.imageanalysis.features.global.ACCID;
import net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.PHOG;
import net.semanticmetadata.lire.imageanalysis.features.global.Tamura;
import net.semanticmetadata.lire.indexers.parallel.ExtractorItem;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.SimpleImageSearchHits;
import net.semanticmetadata.lire.searchers.SimpleResult;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.Bits;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


public class FeatureSimilarity {
    protected TreeSet<SimpleResult> similarDocs = new TreeSet<>(); // Contain similar documents.
    protected String fieldName; // Contain field name of the selected feature.
    protected LireFeature cachedInstance; // Contain feature data of currently processed image in the index.
    protected ExtractorItem extractorItem; // Contain extractor item.


    /**
     * Return a hash map of similar images of size {@code numOfImages}, compared with the feature {@code globalFeature},
     * based on a BufferedImage {@code queryImg} and IndexReader {@code indexReader} provided.
     *
     * @param numOfImages number of similar images returned
     * @param globalFeature GlobalFeature class to compare with (e.g. ACCID.class)
     * @param queryImg a BufferedImage instance representing the query image
     * @param indexReader an IndexReader instance referring to the image index.
     * @return a hash map of similar images with fileName as key and score as value.
     * @throws Exception Exception will be thrown if the feature provided is not supported.
     */
    public SortedSet<Map.Entry<String, Double>> findSimilarImages(int numOfImages, Class<? extends GlobalFeature> globalFeature,
                                                     BufferedImage queryImg, IndexReader indexReader) throws Exception {
        Map<String, Double> listOfImagePaths = new HashMap<>();

        this.extractorItem = new ExtractorItem(globalFeature);
        this.fieldName = extractorItem.getFieldName();
        this.cachedInstance = (GlobalFeature) extractorItem.getExtractorInstance().getClass().newInstance();

        if (extractorItem.isGlobal() && (cachedInstance instanceof ACCID || cachedInstance instanceof Tamura ||
                cachedInstance instanceof PHOG || cachedInstance instanceof OpponentHistogram)) {
            GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder();
            GlobalFeature queryGlobalFeature = globalDocumentBuilder.extractGlobalFeature(queryImg, (GlobalFeature) extractorItem.getExtractorInstance());

            // Find similar images and return the maximum distance between query and result.
            double maxDistance = findSimilar(indexReader, queryGlobalFeature, numOfImages);
            ImageSearchHits searchHits = new SimpleImageSearchHits(this.similarDocs, maxDistance);

            for (int i = 0; i < searchHits.length(); i++) {
                String fileName = indexReader.document(searchHits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
                listOfImagePaths.put(fileName, searchHits.score(i));
            }

        } else {
            throw new Exception("Feature to compare must be either ACCID, TAMURA, PHOG or OppHist.");
        }

        return entriesSortedByValues(listOfImagePaths); // Already sorted in the ascending order of score (aka distance).
    }


    /**
     * @param indexReader an IndexReader instance referring to the image index.
     * @param lireFeature an LireFeature instance containing the feature vector of query image.
     * @return the maximum distance found for normalizing.
     * @throws Exception Exception will be thrown if the feature provided is not supported.
     */
    protected double findSimilar(IndexReader indexReader, LireFeature lireFeature, int maxImg) throws Exception {
        double maxDistance = -1d;

        // Clear the results
        similarDocs.clear();
        // To check whether a document is deleted.
        Bits liveDocs = MultiFields.getLiveDocs(indexReader);
        Document d;
        double tmpDistance;
        int docs = indexReader.numDocs();

        // Process every document from the index and then compare it to the query image.
        for (int i = 0; i < docs; i++) {
            if (indexReader.hasDeletions() && !liveDocs.get(i)) continue; // If it is deleted, just ignore it.

            d = indexReader.document(i);
            tmpDistance = getDistance(d, lireFeature);
            assert (tmpDistance >= 0);

            if (this.similarDocs.size() < maxImg) { // If the array is not full yet,
                this.similarDocs.add(new SimpleResult(tmpDistance, i));
                if (tmpDistance > maxDistance) maxDistance = tmpDistance;
            } else if (tmpDistance < maxDistance) {
                // If it is nearer to the query than at least one of the current set, remove the last one (largest distance).
                this.similarDocs.remove(this.similarDocs.last());
                // Add the new one
                this.similarDocs.add(new SimpleResult(tmpDistance, i));
                // And set our new distance upper limit
                maxDistance = this.similarDocs.last().getDistance();
            }
        }

        return maxDistance;
    }


    /**
     * Similarity method to find the distance between document in the index and feature vector of query image.
     * @param document a Document instance representing an indexed image.
     * @param lireFeature an LireFeature instance containing the feature vector of query image.
     * @return the distance between the given feature and the feature stored in the document.
     * @throws Exception Exception will be thrown if the feature provided is not supported.
     */
    protected double getDistance(Document document, LireFeature lireFeature) throws Exception {
        if (document.getField(fieldName).binaryValue() != null && document.getField(fieldName).binaryValue().length > 0) {
            cachedInstance.setByteArrayRepresentation(document.getField(fieldName).binaryValue().bytes,
                    document.getField(fieldName).binaryValue().offset, document.getField(fieldName).binaryValue().length);

            if (lireFeature instanceof OpponentHistogram) {
                return jsd(cachedInstance.getFeatureVector(), lireFeature.getFeatureVector());
            } else if (lireFeature instanceof Tamura) {
                return distL2(cachedInstance.getFeatureVector(), lireFeature.getFeatureVector());
            } else if (lireFeature instanceof ACCID) {
                return jsd(lireFeature.getFeatureVector(), cachedInstance.getFeatureVector());
            } else if (lireFeature instanceof PHOG) {
                return distL1(lireFeature.getFeatureVector(), cachedInstance.getFeatureVector());
            } else {
                throw new Exception("Feature to compare must be either ACCID, TAMURA, PHOG or OppHist.");
            }

        } else {
            System.out.println("No feature stored in this document! (" + extractorItem.getExtractorClass().getName() + ")");
        }

        return 0d;
    }


    /**
     * Sort a {@code Map} instance by value and return {@code SortedSet} instance
     * @param map a Map instance with image file path as key and distance/score as value
     * @param <K> Class of key
     * @param <V> Class of value
     * @return a {@code SortedSet} instance sorted by value.
     */
    protected <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1;
                }
        );

        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }


    /**
     * Manhattan distance / L1
     *
     * @param h1 histogram 1
     * @param h2 histogram 2
     * @return distance value
     */
    protected double distL1(double[] h1, double[] h2) {
        assert h1.length == h2.length;

        double sum = 0.0D;

        for (int i = 0; i < h1.length; ++i) {
            sum += Math.abs(h1[i] - h2[i]);
        }

        return sum;
    }


    /**
     * Jeffrey Divergence or Jensen-Shannon divergence.
     *
     * @param h1 histogram 1
     * @param h2 histogram 2
     * @return distance value
     */
    protected double jsd(double[] h1, double[] h2) {
        assert h1.length == h2.length;

        double sum = 0.0D;

        for (int i = 0; i < h1.length; ++i) {
            sum += (h1[i] > 0.0D ? h1[i] / 2.0D * Math.log(2.0D * h1[i] / (h1[i] + h2[i])) : 0.0D) + (h2[i] > 0.0D ? h2[i] / 2.0D * Math.log(2.0D * h2[i] / (h1[i] + h2[i])) : 0.0D);
        }

        return sum;
    }


    /**
     * Euclidean distance / L2
     *
     * @param targetFeature target feature vector
     * @param queryFeature query feature vector
     * @return distance value
     */
    protected double distL2(double[] targetFeature, double[] queryFeature) {
        double result = 0.0D;

        for (int i = 0; i < targetFeature.length; ++i) {
            result += Math.pow(targetFeature[i] - queryFeature[i], 2.0D);
        }

        return Math.sqrt(result);
    }
}