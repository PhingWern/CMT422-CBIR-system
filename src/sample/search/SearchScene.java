package sample.search;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram;
import org.apache.lucene.index.IndexReader;
import utils.FeatureSimilarity;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;

public class SearchScene {
    String indexPath = "D:\\Documents\\blablabla\\index";
    IndexReader ir;
    int numOfImagesReturned = 10;

    @FXML
    Button mainMenuBTN;

    @FXML
    public void initialize() {
        // TODO: Should comment out
//        try {
//            ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void redirectToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../main/mainScene.fxml"));
        Stage currentStage = (Stage) mainMenuBTN.getScene().getWindow();
        currentStage.setTitle("CMT422 - CBIR System");
        currentStage.setScene(new Scene(root));
        currentStage.show();
    }

    @FXML
    private void onClickBrowse(ActionEvent actionEvent) throws Exception {
        // **** Replace with File Picker
        File file =  new File("D:\\Documents\\blablabla\\samples\\psm-1.jpg");

        BufferedImage img = null;
        if (file.exists()) {
            try {
                img = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FeatureSimilarity fs = new FeatureSimilarity();
        // Parameters for findSimilarImages: num of similar images to return, type of feature, BufferedImage obj (represent query image), IndexReader obj
        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(numOfImagesReturned, OpponentHistogram.class, img, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(numOfImagesReturned, PHOG.class, img, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(numOfImagesReturned, ACCID.class, img, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImages(numOfImagesReturned, Tamura.class, img, ir);

        // TODO: Display all images to GUI.
        // Display all the paths of similar images and their distance with the query image (sorted in asc)
        similarImages.forEach((entry) -> {
            System.out.println("Image File Path: " + entry.getKey() + "; Distance: " + entry.getValue());
        });
    }
}
