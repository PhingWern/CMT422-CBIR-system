package sample.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MainScene {

    @FXML
    Button indexBTN, browseBTN, searchBTN;

    @FXML
    private void redirectToIndexing(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../index/indexScene.fxml"));
        Stage currentStage = (Stage) indexBTN.getScene().getWindow();
        currentStage.setTitle("CMT422 - CBIR System/indexing");
        currentStage.setScene(new Scene(root));
        currentStage.show();
    }

    @FXML
    private void redirectToBrowsing(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../browse/browseScene.fxml"));
        Stage currentStage = (Stage) browseBTN.getScene().getWindow();
        currentStage.setTitle("CMT422 - CBIR System/browsing");
        currentStage.setScene(new Scene(root));
        currentStage.show();
    }

    @FXML
    private void redirectToSearching(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../search/searchScene.fxml"));
        Stage currentStage = (Stage) searchBTN.getScene().getWindow();
        currentStage.setTitle("CMT422 - CBIR System/searching");
        currentStage.setScene(new Scene(root));
        currentStage.show();
    }

}