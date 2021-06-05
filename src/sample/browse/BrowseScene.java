package sample.browse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import utils.FeatureSimilarity;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedSet;

public class BrowseScene {
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
    private void onClickSearch(ActionEvent actionEvent) throws Exception {
        // *** Determine a document index (from 0 to n-1)
        int docId = 0;

        FeatureSimilarity fs = new FeatureSimilarity();
        // Parameters for findSimilarImages: num of similar images to return, type of feature, document index to query, IndexReader obj
        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImagesByDoc(numOfImagesReturned, OpponentHistogram.class, docId, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImagesByDoc(numOfImagesReturned, PHOG.class, docId, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImagesByDoc(numOfImagesReturned, ACCID.class, docId, ir);
//        SortedSet<Map.Entry<String, Double>> similarImages = fs.findSimilarImagesByDoc(numOfImagesReturned, Tamura.class, docId, ir);

        // TODO: Display all images to GUI.
        // Display all the paths of similar images and their distance with the query image (sorted in asc)
        similarImages.forEach((entry) -> {
            System.out.println("Image File Path: " + entry.getKey() + "; Distance: " + entry.getValue());
        });
    }
}
