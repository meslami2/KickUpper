package kickupper.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import kickupper.Configuration;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    /**
     * Creates the initial index files on disk
     *
     * @param indexPath
     * @return
     * @throws IOException
     */
    private static IndexWriter setupIndex(String indexPath) throws IOException {
        Analyzer analyzer = new SpecialAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46,
                analyzer);
        config.setOpenMode(OpenMode.CREATE);
        config.setRAMBufferSizeMB(2048.0);

        FSDirectory dir;
        IndexWriter writer = null;
        dir = FSDirectory.open(new File(indexPath));
        writer = new IndexWriter(dir, config);

        return writer;
    }

    /**
     * @param indexPath Where to create the index
     * @param inputFile input file contains [count ngram] in each line
     * @throws IOException
     */
    public static void index(String inputFile, String indexPath) {
        try {
            System.out.println("Creating Lucene index from: " + inputFile + " to: " + indexPath);

            FieldType _contentFieldType = new FieldType();
            _contentFieldType.setIndexed(true);
            _contentFieldType.setStored(true);
            _contentFieldType.setStoreTermVectors(true);

            FieldType _freqFieldType = new FieldType();
            _freqFieldType.setStored(true);
//            _freqFieldType.setNumericType(FieldType.NumericType.INT);
            _freqFieldType.setIndexed(false);

            FieldType _idFieldType = new FieldType();
            _idFieldType.setStored(true);
            _idFieldType.setIndexed(false);

            IndexWriter writer = setupIndex(indexPath);
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = null;
            int indexed = 0;
            int ignoreCount =0;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split("\\s+", 4);
                float freq = Integer.parseInt(splits[1]) + Integer.parseInt(splits[2]);
                if (freq < Configuration.FREQ_FILTER){
                	ignoreCount++;
                	continue;
                }
                Document doc = new Document();
                doc.add(new Field("id", splits[0], _idFieldType));
                doc.add(new Field("freqP", splits[1], _freqFieldType));
                doc.add(new Field("freqN", splits[2], _freqFieldType));
                final Field contentField = new Field("content", splits[3], _contentFieldType);
                contentField.setBoost(freq);
                doc.add(contentField);
                writer.addDocument(doc);
                ++indexed;
                if (indexed % 1000 == 0) {
                    System.out.println(" -> indexed " + indexed + " ngrams... ignore#:"+ignoreCount);
                }
            }
            System.out.println(" -> indexed " + indexed + " total docs.");

            br.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        String str = "baba golabi hello hello bib";
        String[] splits = str.split("\\s+", 2);
        System.out.println(splits.length);
        System.out.println(splits[0] + "\n" + splits[1]);
    }
}
