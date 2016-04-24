package org.hyj;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by hyj on 16-4-24.
 */
public class TxtIndexer {
    private static final String INDEX_DIR = "d:\\temp\\index";
    private static final String TXT_DIR = "d:\\temp\\txt";
    private static final FieldType ANALYZED_BUT_NOT_STORED = new FieldType();
    private static final FieldType CREATE_TIME = new FieldType();
    private static final FieldType PATH = new FieldType();

    static {

        ANALYZED_BUT_NOT_STORED.setStored(false);
        ANALYZED_BUT_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        ANALYZED_BUT_NOT_STORED.setTokenized(true);

        CREATE_TIME.setStored(true);
        CREATE_TIME.setIndexOptions(IndexOptions.DOCS);
        CREATE_TIME.setNumericType(FieldType.NumericType.LONG);
        CREATE_TIME.setTokenized(false);

        PATH.setStored(true);
        PATH.setIndexOptions(IndexOptions.NONE);
        PATH.setStoreTermVectors(false);
        PATH.setTokenized(false);
    }

    public static void main(String[] args) throws IOException, ParseException {
        // index();
        search();
    }

    private static void index() throws IOException {
        Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriter writer = new IndexWriter(indexDir, new IndexWriterConfig(new StandardAnalyzer()));

        File txtDir = new File(TXT_DIR);
        String[] files = txtDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".txt"));
            }
        });
        for (String fileName : files) {
            File txtFile = new File(txtDir, fileName);
            Path txtPath = Paths.get(TXT_DIR, fileName);
            Document doc = new Document();
            BasicFileAttributes attrs = Files.readAttributes(txtPath, BasicFileAttributes.class);
            doc.add(new LongField("createdTime", attrs.creationTime().toMillis(), CREATE_TIME));
            doc.add(new Field("content", new FileReader(txtFile), ANALYZED_BUT_NOT_STORED));
            doc.add(new Field("path", txtPath.toAbsolutePath().toString(), PATH));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private static void search() throws IOException, ParseException {
        Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexSearcher is = new IndexSearcher(DirectoryReader.open(indexDir));
        QueryParser parser = new QueryParser("content", new StandardAnalyzer());
        Query query = parser.parse("seaborne");
        indexDir.close();
        TopDocs topDocs = is.search(query, 10);
        System.out.println("Matches: ");
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            System.out.println("Created: " + doc.get("createdTime") + " Path: " + doc.getField("path") + " Content: " + doc.get("content"));
            System.out.println("=====");
        }


    }
}
