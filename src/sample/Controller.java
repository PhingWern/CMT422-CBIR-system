package sample;

import javafx.fxml.FXML;
import net.semanticmetadata.lire.imageanalysis.features.global.ACCID;
import net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.PHOG;
import net.semanticmetadata.lire.imageanalysis.features.global.Tamura;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import utils.FeatureSimilarity;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedSet;


public class Controller {
    @FXML
    public void initialize() throws Exception {
        // =============================================
        // Sample code to use FeatureSimilarity class:
        // =============================================

        // *** Change to the correct index directory path
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\Documents\\blablabla\\liredemo\\index")));

        BufferedImage img = null;
        // *** Change to the query image path
        File f = new File("D:\\Documents\\blablabla\\samples\\psm-1.jpg");
        if (f.exists()) {
            try {
                img = ImageIO.read(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FeatureSimilarity fs = new FeatureSimilarity();
        // Parameters for findSimilarImages: num of similar images to return, type of feature, BufferedImage obj (represent query image), IndexReader obj
        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(15, OpponentHistogram.class, img, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(15, PHOG.class, img, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(15, ACCID.class, img, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(15, Tamura.class, img, ir);

        // Display all the paths of similar images and their distance with the query image (sorted in asc)
        similarImages.forEach((entry) -> {
            System.out.println("Image File Path: " + entry.getKey() + "; Distance: " + entry.getValue());
        });
    }
}
