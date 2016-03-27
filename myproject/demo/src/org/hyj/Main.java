package org.hyj;

import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;

/**
 * Created by hyj on 16-3-27.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // indexFiles("d:\\HyjPersonalData\\LuceneStudy\\lucene-5.5.0-src\\core\\src\\");
        search();
    }

    private static void indexFiles(String dir) {
        IndexFiles.main(new String[] {"-docs", dir});
    }

    private static void search() throws Exception {
        SearchFiles.main(new String[]{});
    }

}
