package sample.browse;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import sample.search.SearchScene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class BrowseScene {
    //declare the index path
    String indexPath = "./index";
    //declare the index reader
    IndexReader ir;

    /*************
     *   main menu
     *   mainMenuBTN - main menu button to go back to main menu
     * ************/
    @FXML
    Button mainMenuBTN;

    /*******************
     *   browse control
     *
     *   ImageBrowsed - image browsed by user (image with id -> value of docIdSpinner - 1 )
     *   docIdSpinner - Spinner that display the index id + 1
     *   browseGrid - grid that display image
     * *****************/
    @FXML
    ImageView ImageBrowsed;

    @FXML
    Spinner docIdSpinner;

    @FXML
    GridPane browseGrid;

    /**
     * run when the Browse Scene Controller is loaded.
     * to initialize the index reader, setup the docIdSpinner and the displayed image
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {

        try {
            //get the index reader through the image path
            ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

            //initialize the spinner to provide choices in the range of 1 - 30
            docIdSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,ir.maxDoc()));
            //define the default number of images returned
            docIdSpinner.getValueFactory().setValue(1);
            //display the first image
            displayImage(0);

        } catch (IOException e) {
            //if the file is not existing in the directory
            //create a new alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //set alert title
            alert.setTitle("Error");
            //set the alert message
            alert.setContentText("Error reading the index! You may need to do indexing first. \n\n" + e.getMessage());
            //display the alert dialog
            alert.show();
            //if encountering any error, print it in the console
            e.printStackTrace();
        }

    }

    /**
     * when menu button clicked
     * return to the main menu
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void redirectToMainMenu(ActionEvent actionEvent) throws IOException {
        // get the fxml and controller of the main scene
        Parent root = FXMLLoader.load(getClass().getResource("/sample/main/mainScene.fxml"));
        // get current stage by using mainMenuBTN in current Scene
        Stage currentStage = (Stage) mainMenuBTN.getScene().getWindow();
        // set the title of the scene
        currentStage.setTitle("CMT422 - CBIR System");
        // set the new scene to the current stage
        currentStage.setScene(new Scene(root));
        // show the stage
        currentStage.show();
    }

    @FXML
    private void onClickSearch(ActionEvent actionEvent) throws Exception {

        // Determine a document index (from 0 to n-1)
        // get the document Id
        int docId = ((Integer) docIdSpinner.getValue()).intValue() - 1;

        //Load search scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/search/searchScene.fxml"));
        Parent root = loader.load();

        //Get controller of search scene
        SearchScene searchController = loader.getController();
        //Pass the docId to search scene
        searchController.setImageId(docId);

        //Show search scene in new window
        Stage currentStage = (Stage) mainMenuBTN.getScene().getWindow();
        //set the title of next stage
        currentStage.setTitle("CMT422 - CBIR System/Search");
        //set the scene to the stage
        currentStage.setScene(new Scene(root));
        //show the stage
        currentStage.show();

    }

    /**
     * triggered when the value of spinner change
     * @throws IOException
     */
    @FXML
    private void onSpinnerChange() throws IOException {
        //get the new docId
        int docID = ((Integer) docIdSpinner.getValue()).intValue() - 1;

        //check id the docId is valid
        if (!(docID < 0 || docID > ir.maxDoc())) {
            //if valid, then display the image
            displayImage(docID);
        } else {
            //if not valid
            if (docID < 0) {
                //if docId less than 0
                //set the value of spinner to 1
                docIdSpinner.getValueFactory().setValue(1);
            } else {
                //if docID more than 0 and number of maximum document
                //set the value to the number of maximum document
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

        try {
            //read the document from index using docId
            Document d = ir.document(docId);
            //declare an empty bufferedImage
            BufferedImage img = null;
            //get the file path of the document
            String file = d.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            //if the file is not a url
            if (!file.startsWith("http:")) {
                //read from local directory
                img = ImageIO.read(new java.io.FileInputStream(file));
            } else {
                //read from url
                img = ImageIO.read(new URL(file));
            }

            // create a writableImage
            WritableImage i = SwingFXUtils.toFXImage(img, null);

            //set the height of the imageBrowsed
            ImageBrowsed.fitHeightProperty().bind(browseGrid.heightProperty().multiply(0.9));
            //set the width of the imageBrowsed
            ImageBrowsed.fitWidthProperty().bind(browseGrid.widthProperty());
            //set the image to the ImageBrowsed (will be displayed in the GUI)
            ImageBrowsed.setImage(i);

        } catch (Exception e) {
            //if any error occurs, print the error to the console.
            System.err.println(e.toString());
        }
    }

}
