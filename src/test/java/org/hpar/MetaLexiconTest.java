package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;

public class MetaLexiconTest extends TestCase {

    public void testFind_tags() throws Exception {
        String data = App.readFile("src/test/extern/index.html");
        MetaLexicon metaLexicon = new MetaLexicon(data, 0, data.length()/2);
        metaLexicon.find();
    }

    long time_n = 0;
    long time_parse = 0;
    int match_n = 0;

    public void testAll() {
        loadAllFiles("src/test/extern/websites");

    }
    public void loadAllFiles(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
        assert files != null;
        for(File file : files) {
            System.out.println(file.getAbsolutePath());
            try {
                forOneFile(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average: "+time_n/(1000000.0 * files.length)+"ms");
        System.out.println("Parser: "+time_parse/(1000000.0 * files.length)+"ms");
        System.out.println("Match: "+match_n+"/"+files.length);
    }

    public void forOneFile(String path) throws IOException {
        String data = App.readFile(path);

        long begin = System.nanoTime();
        MetaLexicon metaLexicon = new MetaLexicon(data, 0, data.length());
        metaLexicon.find();
        if (metaLexicon.match()) {
            match_n++;
        }
        long end = System.nanoTime();
        time_n += (end-begin);

        begin = System.nanoTime();
        Parser.parse(data, "");
        end = System.nanoTime();

        time_parse += (end-begin);
    }
}