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

    @FXML
    Button mainMenuBTN, indexBTN, browseBTN;

    @FXML
    TextField imageDirPath;

    @FXML
    GridPane contentGrid;

    @FXML
    private void redirectToMainMenu(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../main/mainScene.fxml"));
        Stage currentStage = (Stage) mainMenuBTN.getScene().getWindow();
        currentStage.setTitle("CMT422 - CBIR System");
        currentStage.setScene(new Scene(root));
        currentStage.show();
    }

    @FXML
    private void onStartIndexing(ActionEvent actionEvent) throws Exception {

        String path = imageDirPath.getText();
        addProgressBar();
        addIndexLabel();
        Thread indexing = new Thread(new FeatureExtraction(path));
        indexing.start();
    }

    @FXML
    private void onBrowseClick(ActionEvent actionEvent){
        Stage currentStage = (Stage) imageDirPath.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource Directory");

        File selectedFile = directoryChooser.showDialog(currentStage);

        //if a file is selected, enable the search button
        if (selectedFile != null) {
            imageDirPath.clear();
            imageDirPath.appendText(selectedFile.getPath());
            indexBTN.setDisable(false);
            indexBTN.setDefaultButton(true);
        }
    }

    public void addProgressBar(){

        if(contentGrid.getChildren().contains(indexProgress)){
            indexProgress.setProgress(0.0);
        }
        else{
            indexProgress.setProgress(0.0);
            indexProgress.setMaxWidth(Double.MAX_VALUE);
            indexProgress.setMaxHeight(Double.MAX_VALUE);
            GridPane.setValignment(indexProgress, VPos.CENTER);
            GridPane.setHalignment(indexProgress, HPos.CENTER);
            GridPane.setMargin(indexProgress, new Insets(20,5,20,5));
            contentGrid.add(indexProgress, 1, 5, 3, 1);
        }

    }

    public void addIndexLabel(){
        if(contentGrid.getChildren().contains(indexLabel)){
            indexLabel.setText(" Starting ... ");
        }
        else{
            indexLabel.setText(" Starting ... ");
            indexLabel.fontProperty().setValue(new Font(18));
            indexLabel.setMaxWidth(Double.MAX_VALUE);
            indexLabel.setMaxHeight(Double.MAX_VALUE);
            GridPane.setValignment(indexLabel, VPos.TOP);
            GridPane.setHalignment(indexLabel, HPos.CENTER);
            GridPane.setMargin(indexLabel, new Insets(20,10,20,10));
            contentGrid.add(indexLabel, 1, 6, 3, 1);
        }

    }
}
