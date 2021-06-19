package utils;

import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.*;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FeatureExtraction {

    public void run(String path) throws Exception {
//        Checking if the directory is empty
        boolean isAccepted = false;
        if(path.length()>0){
            File f = new File(path);
            if(f.exists() && f.isDirectory())
                isAccepted=true;
        }

        if(!isAccepted){
            throw new Exception("No directory is given. Indexing cannot undergone.");
        }

//        Get image from directory
        ArrayList<String> images = FileUtils.readFileLines(new File(path),true);

//        create document and store all files
        GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(ACCID.class);
//        feature need to extract
        //globalDocumentBuilder.addExtractor(ACCID.class);
        globalDocumentBuilder.addExtractor(PHOG.class);
        globalDocumentBuilder.addExtractor(Tamura.class);
        globalDocumentBuilder.addExtractor(OpponentHistogram.class);

//        Create Lucene index writer
        IndexWriter writer = LuceneUtils.createIndexWriter("index",true,LuceneUtils.AnalyzerType.WhitespaceAnalyzer);

        for (Iterator<String> it = images.iterator(); it.hasNext(); ){
            String imageFilePath = it.next();
            try{
                BufferedImage bufferedImage = ImageIO.read(new FileInputStream(imageFilePath));
                System.out.println(imageFilePath);
                Document document = globalDocumentBuilder.createDocument(bufferedImage,imageFilePath);
                writer.addDocument(document);
            }catch (Exception e){
                throw new Exception(e);
            }

        }

//        Close writer
        LuceneUtils.closeWriter(writer);

    }
}
