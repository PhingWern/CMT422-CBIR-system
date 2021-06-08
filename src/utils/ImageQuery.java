package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.global.ACCID;
import net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.PHOG;
import net.semanticmetadata.lire.imageanalysis.features.global.Tamura;
import net.semanticmetadata.lire.indexers.parallel.ExtractorItem;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.SimpleImageSearchHits;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

public class ImageQuery {

    public ImageQuery(){}

    public Image findImageByDocId(IndexReader ir, int docId) throws IOException {
        Document d = ir.document(docId);
        BufferedImage img = null;
        String file = d.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
        if (!file.startsWith("http:")) {
            img = ImageIO.read(new java.io.FileInputStream(file));
        } else {
            img = ImageIO.read(new URL(file));
        }

        return SwingFXUtils.toFXImage(img, null);
    }

    public ImageSearchHits findImageByVectorFile(int numOfImages, Class<? extends GlobalFeature> globalFeature,
                                                 BufferedImage queryImg, IndexReader indexReader){
        //Reserved
        return null;
    }

}
