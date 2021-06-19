package sample.search;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;


public class SearchScene {
    String indexPath = "index";
    IndexReader ir;

    //initialize the required value
    int numOfImagesReturned = 9;
    //String indexSearcherType = "Opponent Histogram";
    //String rankingType = "Opponent Histogram";

    //variables used for display image in gridpane
    int currentRow = 0;
    int currentCol = 0;

    //image id - search by doc
    int docId = -1;

    /*************
     *   main menu
     * ************/
    @FXML
    Button mainMenuBTN;

    /*******************
     *   search control
     * *****************/
    @FXML
    private Button browseBTN, searchBTN;

    @FXML
    private TextField filePathTextField;

    /*******************
     *   setting control
     * *****************/
    @FXML
    private Button applyBTN, resetBTN;

    @FXML
    private ChoiceBox<String> indexSearchChoice, rankingChoice;

    @FXML
    private Spinner<Integer> numberOfImageSpinner;

    /*******************
     *   output area
     * *****************/
    @FXML
    private ScrollPane imageOutputPane;


    /*******************
     *   constructor
     * *****************/



    /*******************
     *   functions
     * *****************/
    @FXML
    public void initialize(){

        // TODO: Should comment out
        try {
            ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initialize the choice box for index searcher
        indexSearchChoice.getItems().add(0, "Opponent Histogram");
        indexSearchChoice.getItems().add(1, "PHOG");
        indexSearchChoice.getItems().add(2, "ACCID");
        indexSearchChoice.getItems().add(3, "Tamura");

        //define the default index searcher
        indexSearchChoice.getSelectionModel().select(0);
        //indexSearcherType = "Opponent Histogram";

        //initialize the choice box for ranking features
        rankingChoice.getItems().add(0, "Opponent Histogram");
        rankingChoice.getItems().add(1, "PHOG");
        rankingChoice.getItems().add(2, "ACCID");
        rankingChoice.getItems().add(3, "Tamura");

        //define the default value of ranking features
        rankingChoice.getSelectionModel().select(0);
        //rankingType = "Opponent Histogram";

        //initialize the spinner to provide choices in the range of 1 - 30
        numberOfImageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,30));
        //define the default number of images returned
        numberOfImageSpinner.getValueFactory().setValue(numOfImagesReturned);

    }

    public void setImageId(int docId) throws Exception {
        this.docId = docId;
        System.out.println(this.docId);
        //perform matching
        SortedSet<Map.Entry<String, Double>> similarImages = getSimilarImages(docId);
        displayImageInGrid(similarImages);
    }

    /**
     * when menu button clicked
     * return to the main menu
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void redirectToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../main/mainScene.fxml"));
        Stage currentStage = (Stage) mainMenuBTN.getScene().getWindow();
        currentStage.setTitle("CMT422 - CBIR System");
        currentStage.setScene(new Scene(root));
        currentStage.show();
    }

    /**
     * To start search
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void onClickSearch(ActionEvent actionEvent) throws Exception {

        // Read file from the path given
        File file =  new File(filePathTextField.getText());

        BufferedImage img = null;

        //read image to BufferedImage
        if (file.exists()) {
            try {
                img = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //perform matcing
        SortedSet<Map.Entry<String, Double>> similarImages = getSimilarImages(img);
        displayImageInGrid(similarImages);

    }

    /**
     * To select image from file directory
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void onClickBrowse(ActionEvent actionEvent) throws Exception {
        Stage currentStage = (Stage) filePathTextField.getScene().getWindow();

        //open file chooser window
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        //only allow image files to be chosen
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        //if a file is selected, enable the search button
        if (selectedFile != null) {
            filePathTextField.clear();
            filePathTextField.appendText(selectedFile.getPath());
            searchBTN.setDisable(false);
        }
    }

    /**
     * to reset the settings
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void onClickReset(ActionEvent actionEvent) throws Exception {

        rankingChoice.getSelectionModel().select(0);
        indexSearchChoice.getSelectionModel().select(0);
        numberOfImageSpinner.getValueFactory().setValue(9);

    }

    /**
     * create and configure a GridPane
     * @return a configured GridPane
     */
    private GridPane createGrid(){

        GridPane imageGrid = new GridPane();
        imageGrid.setPadding(new Insets(10));
        imageGrid.setGridLinesVisible(true);
        imageGrid.setAlignment(Pos.CENTER);
        //imageGrid.alignmentProperty().setValue(Pos.CENTER);

        imageGrid.prefWidthProperty().bind(imageOutputPane.widthProperty());
        imageGrid.setMinWidth(Region.USE_PREF_SIZE);
        //imageGrid.setMaxWidth(Region.USE_PREF_SIZE);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(33.333);

        imageGrid.getColumnConstraints().addAll(columnConstraints, columnConstraints);

        return imageGrid;
    }

    /**
     * create and configure an image node
     * the image node will be embedded in a HBox for padding and alignment purpose
     * @param filePath
     * @return a HBox containing imageView
     * @throws IOException
     */
    private HBox createImageNode(String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        ImageView imgView = new ImageView();
        imgView.setPreserveRatio(true);
        imgView.fitWidthProperty().bind(imageOutputPane.widthProperty().divide(4));

        imgView.setImage(SwingFXUtils.toFXImage(image, null));
        final HBox pictureRegion = new HBox();
        pictureRegion.alignmentProperty().setValue(Pos.CENTER);
        pictureRegion.setPadding(new Insets(30));
        pictureRegion.getChildren().add(imgView);

        return pictureRegion;
    }

    /**
     * perform matching using selected index searcher
     * @param img
     * @return a sorted set contains entries of filepath and distance
     * @throws Exception
     */
    private SortedSet<Map.Entry<String, Double>> getSimilarImages(BufferedImage img) throws Exception {

        // Parameters for findSimilarImages: num of similar images to return, type of feature, BufferedImage obj (represent query image), IndexReader obj
        FeatureSimilarity fs = new FeatureSimilarity();

        if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 0)
            return fs.findSimilarImages(numOfImagesReturned, OpponentHistogram.class, img, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 1)
            return fs.findSimilarImages(numOfImagesReturned, PHOG.class, img, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 2)
            return fs.findSimilarImages(numOfImagesReturned, ACCID.class, img, ir);
        else// if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 3)
            return fs.findSimilarImages(numOfImagesReturned, Tamura.class, img, ir);

    }

    /**
     * perform matching using selected index searcher
     * @param docId
     * @return a sorted set contains entries of filepath and distance
     * @throws Exception
     */
    private SortedSet<Map.Entry<String, Double>> getSimilarImages(int docId) throws Exception {

        // Parameters for findSimilarImages: num of similar images to return, type of feature, BufferedImage obj (represent query image), IndexReader obj
        FeatureSimilarity fs = new FeatureSimilarity();

        if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 0)
            return fs.findSimilarImagesByDoc(numOfImagesReturned, OpponentHistogram.class, docId, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 1)
            return fs.findSimilarImagesByDoc(numOfImagesReturned, PHOG.class, docId, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 2)
            return fs.findSimilarImagesByDoc(numOfImagesReturned, ACCID.class, docId, ir);
        else// if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 3)
            return fs.findSimilarImagesByDoc(numOfImagesReturned, Tamura.class, docId, ir);

    }
    private void displayImageInGrid(SortedSet<Map.Entry<String, Double>> similarImages){
        //declare the GridPane and do some configurations
        GridPane imageGrid = createGrid();
        //for display in grid pane
        currentRow = 0;
        currentCol = 0;

        // Display all images to GUI.
        // Display all the paths of similar images and their distance with the query image (sorted in asc)
        similarImages.forEach((entry) -> {
            System.out.println("Image File Path: " + entry.getKey() + "; Distance: " + entry.getValue());

            try {

                //add image node to the grid pane
                imageGrid.addRow(currentRow, createImageNode(entry.getKey()));

                currentCol++;
                if(currentCol % 3 == 0){
                    currentRow++;
                    currentCol = 0;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        //append the image grid to the output pane
        imageOutputPane.setContent(imageGrid);
    }

}
