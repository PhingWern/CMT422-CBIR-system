package utils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.*;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import sample.index.IndexScene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FeatureExtraction implements Runnable{
    // implements Runnable to enable the class to run in thread
    // declare the path
    private String path;

    // constructor of the class, set the directory path
    public FeatureExtraction(String path){
        this.path = path;
    }

    @Override
    public void run() {
        // Checking if the directory is empty
        boolean isAccepted = false;
        if(path.length()>0){
            File f = new File(path);
            if(f.exists() && f.isDirectory())
                isAccepted=true;
        }

        if(!isAccepted){
            try {
                throw new Exception("No directory is given. Indexing cannot undergone.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            // Get image from directory
            ArrayList<String> images = FileUtils.readFileLines(new File(path),true);

            // create document and store all files
            GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(ACCID.class);
            // feature need to extract
            //globalDocumentBuilder.addExtractor(ACCID.class);
            globalDocumentBuilder.addExtractor(PHOG.class);
            globalDocumentBuilder.addExtractor(Tamura.class);
            globalDocumentBuilder.addExtractor(OpponentHistogram.class);

            // Create Lucene index writer
            IndexWriter writer = LuceneUtils.createIndexWriter("index",true,LuceneUtils.AnalyzerType.WhitespaceAnalyzer);

            // initialize the i index
            int i = 1;
            // for each image in the directory
            for (Iterator<String> it = images.iterator(); it.hasNext(); ){
                //
                String imageFilePath = it.next();

                try{
                    BufferedImage bufferedImage = ImageIO.read(new FileInputStream(imageFilePath));
                    Document document = globalDocumentBuilder.createDocument(bufferedImage,imageFilePath);
                    writer.addDocument(document);
                    // set the progress of progress bar in index scene
                    IndexScene.indexProgress.setProgress(i / (double)images.size());
                    // declare finalI
                    int finalI = i;
                    // platfrom.runlater to update the GUI outside the thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // update the text in indexLabel
                            IndexScene.indexLabel.setText(" Indexing images ... ( " + finalI + " / " + images.size() + " )");
                        }
                    });
                }catch (Exception e){
                    // if any error occurs, print it to the console
                    throw new Exception(e);
                }
                i++;
            }

            // Close writer
            LuceneUtils.closeWriter(writer);
            // platfrom.runlater to update the GUI outside the thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // update the text in indexLabel
                    IndexScene.indexLabel.setText(" Finish Indexing! ");
                }
            });

        } catch (Exception e) {
            // if any error occurs, print it to the console
            e.printStackTrace();
        }


    }

}
