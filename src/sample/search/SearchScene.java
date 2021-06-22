package sample.search;

import com.sun.prism.Image;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
    //declare the index path
    String indexPath = "index";
    //declare the index reader
    IndexReader ir;

    //initialize the required value
    int numOfImagesReturned = 9;

    //row and column variables used for display image in gridpane
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
     *
     *   searchBTN - search button to trigger search by image
     *   filePathTextField - text field that shows image file path
     * *****************/

    @FXML
    private Button searchBTN;

    @FXML
    private TextField filePathTextField;

    /*******************
     *   setting control
     *
     *   indexSearchChoice - display the type of index searcher
     *   numberOfImageSpinner - number spinner for user to set how many image to return after matching
     * *****************/
    @FXML
    private ChoiceBox<String> indexSearchChoice; // rankingChoice

    @FXML
    private Spinner<Integer> numberOfImageSpinner;

    /*******************
     *   output area
     *
     *   imageOutputPane - pane to hold the image gridpane (display image)
     * *****************/
    @FXML
    private ScrollPane imageOutputPane;



    /*******************
     *   functions
     * *****************/

    @FXML
    /**
     * run when the Search Scene Controller is loaded.
     * to initialize the index reader, setup the indexSearchChoice and numberOfImageSpinner
     */
    public void initialize(){

        try {
            //get the index reader through the image path
            ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        } catch (IOException e) {
            //if encountering any error, print it in the console
            e.printStackTrace();
        }

        //initialize the choice box for index searcher
        //insert Opponent Histogram with index 0
        indexSearchChoice.getItems().add(0, "Opponent Histogram");
        //insert PHOG with index 1
        indexSearchChoice.getItems().add(1, "PHOG");
        //insert ACCID with index 2
        indexSearchChoice.getItems().add(2, "ACCID");
        //insert Tamura with index 3
        indexSearchChoice.getItems().add(3, "Tamura");

        //define the default index searcher
        indexSearchChoice.getSelectionModel().select(0);
        //initialize the spinner to provide choices in the range of 1 - 30, maximum number of images to display is 30
        numberOfImageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9,30));
        //define the default number of images returned
        numberOfImageSpinner.getValueFactory().setValue(numOfImagesReturned);

        //if the inserted value is not valid, reset it with the value in value factory
        numberOfImageSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                numOfImagesReturned = numberOfImageSpinner.getValueFactory().getValue();
            }
        });
    }

    /**
     * the docId is passed from browse scene
     * it is triggered by search button at browse scene
     * @param docId - the document id of chosen image
     * @throws Exception
     */
    public void setImageId(int docId) throws Exception {
        //set the docId
        this.docId = docId;

        //perform matching
        SortedSet<Map.Entry<String, Double>> similarImages = getSimilarImages(docId);
        //display the image result in grid pane
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
        // get the fxml and controller of the main scene
        Parent root = FXMLLoader.load(getClass().getResource("../main/mainScene.fxml"));
        // get current stage by using mainMenuBTN in current Scene
        Stage currentStage = (Stage) mainMenuBTN.getScene().getWindow();
        // set the title of the scene
        currentStage.setTitle("CMT422 - CBIR System");
        // set the new scene to the current stage
        currentStage.setScene(new Scene(root));
        // show the stage
        currentStage.show();
    }

    /**
     * To start search and read the image given
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void onClickSearch(ActionEvent actionEvent) throws Exception {

        // Read file from the path given
        File file =  new File(filePathTextField.getText());

        //declare an empty buffered image
        BufferedImage img = null;

        //read image to BufferedImage
        //check if the image file is existing in the directory
        if (file.exists()) {
            //if the file exists
            try {
                //read the image to the buffered image
                img = ImageIO.read(file);
                // perform matching
                SortedSet<Map.Entry<String, Double>> similarImages = getSimilarImages(img);
                //display the images in grid pane
                displayImageInGrid(similarImages);
            } catch (IOException e) {
                //if encountering any error, print it in the console
                e.printStackTrace();
            }
        }
        else{
            //if the file is not existing in the directory
            //create a new alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //set alert title
            alert.setTitle("Input Error");
            //set the alert message
            alert.setContentText("The file not found! Check your image path.");
            //display the alert dialog
            alert.show();
        }

    }

    /**
     * To select image from file directory
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void onClickBrowse(ActionEvent actionEvent) throws Exception {
        // get current stage by using filePathTextField in current Scene
        Stage currentStage = (Stage) filePathTextField.getScene().getWindow();

        //open file chooser window
        FileChooser fileChooser = new FileChooser();
        //set the title of file chooser
        fileChooser.setTitle("Open Resource File");

        //only allow image files to be chosen
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        //get the selected file
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        // if the selected file is not null
        if (selectedFile != null) {

            // clear the text field
            filePathTextField.clear();
            // insert the new path to the text field
            filePathTextField.appendText(selectedFile.getPath());
            // enable the search button
            searchBTN.setDisable(false);
        }
    }

    /**
     * to reset the settings
     * @param actionEvent
     */
    @FXML
    private void onClickReset(ActionEvent actionEvent) {
        //reset the index searcher to Opponent Histogram
        indexSearchChoice.getSelectionModel().select(0);
        //reset the number of displayed image to 9
        numberOfImageSpinner.getValueFactory().setValue(9);
    }

    /**
     * triggered when value of numberOfImageSpinner changes
     * @param actionEvent
     */
    @FXML
    private void onNumberOfImageChanged(ActionEvent actionEvent) {
        //set the value of numOfImagesReturned
        numOfImagesReturned = numberOfImageSpinner.getValueFactory().getValue();
    }

    /**
     * create and configure a GridPane
     * @return a configured GridPane
     */
    private GridPane createGrid(){
        //create a new grid pane
        GridPane imageGrid = new GridPane();
        //set padding of imageGrid
        imageGrid.setPadding(new Insets(10));
        //set the grid line visible
        imageGrid.setGridLinesVisible(true);
        //set the alignment to be center
        imageGrid.setAlignment(Pos.CENTER);
        //set the preference width property to bind with imageOutputPane
        imageGrid.prefWidthProperty().bind(imageOutputPane.widthProperty());
        //set the min width to use the preference width
        imageGrid.setMinWidth(Region.USE_PREF_SIZE);
        //create a new column constraint
        ColumnConstraints columnConstraints = new ColumnConstraints();
        //set for each column to be in 33.33% of the width
        columnConstraints.setPercentWidth(33.333);
        //apply the defined column constraint to the imageGrid
        imageGrid.getColumnConstraints().addAll(columnConstraints, columnConstraints);
        //return configured grid
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
        //read the image to the BufferedImage
        BufferedImage image = ImageIO.read(new File(filePath));
        //create new image view
        ImageView imgView = new ImageView();
        //preserve the ratio of the image
        imgView.setPreserveRatio(true);
        //set the width of the image to bind with the 1/4 width of imageOutputPane
        imgView.fitWidthProperty().bind(imageOutputPane.widthProperty().divide(4));
        //convert the BufferedImage to Image and set into imageView
        imgView.setImage(SwingFXUtils.toFXImage(image, null));

        //create a new Hbox
        final HBox pictureRegion = new HBox();
        //create a new Button
        final Button pictureButton = new Button();
        //set the alignment of Hbox to center
        pictureRegion.alignmentProperty().setValue(Pos.CENTER);
        //set the padding of the Hbox
        pictureRegion.setPadding(new Insets(30));

        //create a context menu
        final ContextMenu contextMenu = new ContextMenu();
        //create a "view" menuitem
        MenuItem view = new MenuItem("View image");
        //when the "view image" menuitem is clicked
        view.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // get current stage using the searchBTN in current scene
                Stage currentStage = (Stage) searchBTN.getScene().getWindow();
                //create a new Stage
                final Stage dialog = new Stage();
                //initialize the modality of the dialog
                dialog.initModality(Modality.APPLICATION_MODAL);
                //set the owner of the dialog to be current scene
                dialog.initOwner(currentStage);
                //set the file path as title
                dialog.setTitle(filePath);
                //create a new Vbox
                VBox dialogVbox = new VBox(80);
                //create a new ImageView
                ImageView imgView = new ImageView();
                //set the image to the imageView
                imgView.setImage(SwingFXUtils.toFXImage(image, null));
                //insert the imageView to the Vbox
                dialogVbox.getChildren().add(new ScrollPane(imgView));
                //insert the Vbox to the new dialog scene
                Scene dialogScene = new Scene(dialogVbox);
                //insert the dialog scene to the dialog Stage
                dialog.setScene(dialogScene);
                //show the dialog
                dialog.show();
            }
        });

        //create a "search" menuitem
        MenuItem search = new MenuItem("Search by image");
        //when the "Search by image" menuitem is clicked
        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //declare an empty sortedset
                SortedSet<Map.Entry<String, Double>> similarImages = null;
                try {
                    //try the matching with given image
                    similarImages = getSimilarImages(image);
                    //display the images in the grid
                    displayImageInGrid(similarImages);
                } catch (Exception e) {
                    //if any error occurs, print the error in console
                    e.printStackTrace();
                }
            }
        });

        //add the view and search menuitem to context menu
        contextMenu.getItems().addAll(view, search);
        //attach the context menu to the picture button
        //the context menu will show up when user right-click the picture button
        pictureButton.setContextMenu(contextMenu);
        //when the picture button is clicked
        pictureButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //it is clicked by left-click
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    //is double click
                    if(event.getClickCount() == 2){
                        //declare an empty sorted set
                        SortedSet<Map.Entry<String, Double>> similarImages = null;
                        try {
                            //try the matching with given image
                            similarImages = getSimilarImages(image);
                            //display the images in the grid
                            displayImageInGrid(similarImages);
                        } catch (Exception e) {
                            //if any error occurs, print the error in console
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //set the image view to the picture button
        pictureButton.setGraphic(imgView);
        //add the picture button to Hbox
        pictureRegion.getChildren().add(pictureButton);

        //return the Hbox that contains pictureButton and image
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
            //perform similarity matching with OpponentHistogram Index Searcher
            return fs.findSimilarImages(numOfImagesReturned, OpponentHistogram.class, img, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 1)
            //perform similarity matching with PHOG Index Searcher
            return fs.findSimilarImages(numOfImagesReturned, PHOG.class, img, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 2)
            //perform similarity matching with ACCID Index Searcher
            return fs.findSimilarImages(numOfImagesReturned, ACCID.class, img, ir);
        else
            //perform similarity matching with Tamura Index Searcher
            return fs.findSimilarImages(numOfImagesReturned, Tamura.class, img, ir);
    }

    /**
     * perform matching using selected index searcher
     * @param docId - document Id in index
     * @return a sorted set contains entries of filepath and distance
     * @throws Exception
     */
    private SortedSet<Map.Entry<String, Double>> getSimilarImages(int docId) throws Exception {

        // Parameters for findSimilarImages: num of similar images to return, type of feature, BufferedImage obj (represent query image), IndexReader obj
        FeatureSimilarity fs = new FeatureSimilarity();

        if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 0)
            //perform similarity matching with OpponentHistogram Index Searcher
            return fs.findSimilarImagesByDoc(numOfImagesReturned, OpponentHistogram.class, docId, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 1)
            //perform similarity matching with PHOG Index Searcher
            return fs.findSimilarImagesByDoc(numOfImagesReturned, PHOG.class, docId, ir);
        else if (indexSearchChoice.getSelectionModel().getSelectedIndex() == 2)
            //perform similarity matching with ACCID Index Searcher
            return fs.findSimilarImagesByDoc(numOfImagesReturned, ACCID.class, docId, ir);
        else
            //perform similarity matching with Tamura Index Searcher
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

            try {

                //create and add image node to the grid pane
                imageGrid.addRow(currentRow, createImageNode(entry.getKey()));

                //increment current column index
                currentCol++;
                //if there is already 3 column
                if(currentCol % 3 == 0){
                    //increment current row index (go to next row)
                    currentRow++;
                    //restart from first column
                    currentCol = 0;
                }

            } catch (IOException e) {
                //if any error occurs, print it in the console
                e.printStackTrace();
            }

        });

        //append the image grid to the output pane
        imageOutputPane.setContent(imageGrid);
    }
}
