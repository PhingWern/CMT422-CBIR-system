package sample.index;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.lucene.store.Directory;
import utils.FeatureExtraction;
//import utils.FeatureExtraction;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class IndexScene {
    public static ProgressBar indexProgress = new ProgressBar();
    public static Label indexLabel = new Label();

    /*************
     *   index control
     *
     *   mainMenuBTN - main menu button to go back to main menu
     *   indexBTN - index button to start indexing operation
     *   browseBTN - browse button to open directory chooser
     *   imageDirPath - Text field to show selected image directory path
     *   contentGrid - content grid to display the controls element in correct manner
     * ************/
    @FXML
    Button mainMenuBTN, indexBTN, browseBTN;

    @FXML
    TextField imageDirPath;

    @FXML
    GridPane contentGrid;

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
     * when the indexBTN is clicked
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void onStartIndexing(ActionEvent actionEvent) throws Exception {
        // get the path to the image directory
        String path = imageDirPath.getText();
        //add a progress bar
        addProgressBar();
        //add label to display indexing progress
        addIndexLabel();
        //create a thread to run indexing of images
        Thread indexing = new Thread(new FeatureExtraction(path));
        //start the indexing thread
        indexing.start();
    }

    /**
     * when browseBTN is clicked
     * @param actionEvent
     */
    @FXML
    private void onBrowseClick(ActionEvent actionEvent){
        // get current stage by using imageDirPath in current Scene
        Stage currentStage = (Stage) imageDirPath.getScene().getWindow();

        //create a directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        //set the title of directory chooser
        directoryChooser.setTitle("Open Resource Directory");

        //shows the dialog of directory chooser and get the path of selected directory
        File selectedFile = directoryChooser.showDialog(currentStage);

        //if a file is selected
        if (selectedFile != null) {
            //clear the directory path
            imageDirPath.clear();
            //display the new directory path to the text field
            imageDirPath.appendText(selectedFile.getPath());
            //enable the index button
            indexBTN.setDisable(false);
            //set indexBTN as default button
            indexBTN.setDefaultButton(true);
        }
    }

    /**
     * add a progress bar to the contentGrid
     */
    public void addProgressBar(){

        //if there is already a progress bar
        if(contentGrid.getChildren().contains(indexProgress)){
            //reset the progress to 0.0
            indexProgress.setProgress(0.0);
        }
        else{
            //if there is no progress bar shown
            //set the progress to 0.0
            indexProgress.setProgress(0.0);
            //set the maximum width to MAX_VALUE
            indexProgress.setMaxWidth(Double.MAX_VALUE);
            //set the maximum height to MAX_VALUE
            indexProgress.setMaxHeight(Double.MAX_VALUE);
            //set the Valignment of the gridpane for progress bar
            GridPane.setValignment(indexProgress, VPos.CENTER);
            //set the Halignment of the gridpane for progress bar
            GridPane.setHalignment(indexProgress, HPos.CENTER);
            //set the margin
            GridPane.setMargin(indexProgress, new Insets(20,5,20,5));
            //insert the progress bar to column 1 and row 5 with column span of 3
            contentGrid.add(indexProgress, 1, 5, 3, 1);
        }

    }

    /**
     * add index label to the content grid
     */
    public void addIndexLabel(){
        //if there is already a progress bar
        if(contentGrid.getChildren().contains(indexLabel)){
            //reset the label to starting...
            indexLabel.setText(" Starting ... ");
        }
        else{
            //set the label to starting
            indexLabel.setText(" Starting ... ");
            //set the font size of the label
            indexLabel.fontProperty().setValue(new Font(18));
            //set the max width of the label
            indexLabel.setMaxWidth(Double.MAX_VALUE);
            //set the max height of the label
            indexLabel.setMaxHeight(Double.MAX_VALUE);
            //set the Valignment of the gridpane for indexLabel
            GridPane.setValignment(indexLabel, VPos.TOP);
            //set the Halignment of the gridpane for indexLabel
            GridPane.setHalignment(indexLabel, HPos.CENTER);
            //set the margin
            GridPane.setMargin(indexLabel, new Insets(20,10,20,10));
            //insert the label to column 1 and row 6 with column span of 3
            contentGrid.add(indexLabel, 1, 6, 3, 1);
        }

    }
}
