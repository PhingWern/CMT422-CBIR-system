package sample.browse;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.ACCID;
import net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.PHOG;
import net.semanticmetadata.lire.imageanalysis.features.global.Tamura;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import utils.FeatureSimilarity;
import utils.ImageQuery;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedSet;

public class BrowseScene {
    String indexPath = "D:\\Documents\\blablabla\\index";
    IndexReader ir;
    int numOfImagesReturned = 1;

    @FXML
    Button mainMenuBTN;

    @FXML
    ImageView ImageBrowsed;

    @FXML
    Spinner docIdSpinner;

    @FXML
    GridPane browseGrid;

    @FXML
    public void initialize() throws IOException {
        // TODO: Should comment out
//        try {
//            ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //initialize the spinner to provide choices in the range of 1 - 30
        docIdSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,30));
        //define the default number of images returned
        docIdSpinner.getValueFactory().setValue(numOfImagesReturned);

        displayImage(1);
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

        // TODO: implement change scene from browse to search
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

    @FXML
    private void onSpinnerChange() throws IOException {
        int docID = ((Integer) docIdSpinner.getValue()).intValue() - 1;

        if (!(docID < 0 || docID > ir.maxDoc())) {
            displayImage(docID);
        } else {
            if (docID < 0) {
                docIdSpinner.getValueFactory().setValue(1);
            } else {
                docIdSpinner.getValueFactory().setValue(ir.maxDoc());
            }
        }
    }

    /**
     * display image in a pre-defined ImageView
     * @param docId
     * @return a HBox containing imageView
     * @throws IOException
     */
    private void displayImage(int docId) throws IOException {
        // TODO: search doc by id and display image
        try {
            //TODO: Dunno can work or not, need to be test after indexing module is implemented
            //search image by doc Id
            ImageQuery iq = new ImageQuery();

            ImageBrowsed.fitHeightProperty().bind(browseGrid.heightProperty().multiply(0.9));
            ImageBrowsed.fitWidthProperty().bind(browseGrid.widthProperty());
            ImageBrowsed.setImage(iq.findImageByDocId(ir, docId));

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}
