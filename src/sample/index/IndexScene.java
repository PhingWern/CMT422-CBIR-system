package sample.index;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.lucene.store.Directory;
import utils.FeatureExtraction;
//import utils.FeatureExtraction;

import java.io.File;
import java.io.IOException;

public class IndexScene {

    @FXML
    Button mainMenuBTN, indexBTN, browseBTN;

    @FXML
    TextField imageDirPath;

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

        FeatureExtraction featureExtraction = new FeatureExtraction();
        featureExtraction.run(path);
    }

    @FXML
    private void onBrowseClick(ActionEvent actionEvent){
        Stage currentStage = (Stage) imageDirPath.getScene().getWindow();

        //open file chooser window
        //FileChooser fileChooser = new FileChooser();
        //fileChooser.setTitle("Open Resource File");
        //only allow image files to be chosen
        //fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Directory"));
        //File selectedFile = fileChooser.showOpenDialog(currentStage);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource Directory");

        File selectedFile = directoryChooser.showDialog(currentStage);

        //if a file is selected, enable the search button
        if (selectedFile != null) {
            imageDirPath.clear();
            imageDirPath.appendText(selectedFile.getPath());
            indexBTN.setDisable(false);
        }
    }
}
