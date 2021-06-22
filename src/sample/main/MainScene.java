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

    /*************
     *   main menu
     *   indexBTN - index button to go to index scene
     *   browseBTN - browse button to go to browse scene
     *   searchBTN - search button to go to search scene
     * ************/
    @FXML
    Button indexBTN, browseBTN, searchBTN;

    /**
     * when the indexBTN is clicked
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void redirectToIndexing(ActionEvent actionEvent) throws IOException {
        // get the fxml and controller of the index scene
        Parent root = FXMLLoader.load(getClass().getResource("../index/indexScene.fxml"));
        // get current stage by using indexBTN in current Scene
        Stage currentStage = (Stage) indexBTN.getScene().getWindow();
        // set the title of the scene
        currentStage.setTitle("CMT422 - CBIR System/indexing");
        // set the new scene to the current stage
        currentStage.setScene(new Scene(root));
        // show the stage
        currentStage.show();
    }

    /**
     * when the browseBTN is clicked
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void redirectToBrowsing(ActionEvent actionEvent) throws IOException {
        // get the fxml and controller of the browse scene
        Parent root = FXMLLoader.load(getClass().getResource("../browse/browseScene.fxml"));
        // get current stage by using browseBTN in current Scene
        Stage currentStage = (Stage) browseBTN.getScene().getWindow();
        // set the title of the scene
        currentStage.setTitle("CMT422 - CBIR System/browsing");
        // set the new scene to the current stage
        currentStage.setScene(new Scene(root));
        // show the stage
        currentStage.show();
    }

    /**
     * when the searchBTN is clicked
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void redirectToSearching(ActionEvent actionEvent) throws IOException {
        // get the fxml and controller of the search scene
        Parent root = FXMLLoader.load(getClass().getResource("../search/searchScene.fxml"));
        // get current stage by using searchBTN in current Scene
        Stage currentStage = (Stage) searchBTN.getScene().getWindow();
        // set the title of the scene
        currentStage.setTitle("CMT422 - CBIR System/searching");
        // set the new scene to the current stage
        currentStage.setScene(new Scene(root));
        // show the stage
        currentStage.show();
    }

}