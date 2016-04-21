package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;

public class MetaLexiconTest extends TestCase {

    public void testFind_tags() throws Exception {
        String data = App.readFile("src/test/extern/websites/北京航空航天大学图书馆 _ 北航图书馆 _ Library Of Beihang University _ BUAALIB.html");
        MetaLexicon metaLexicon = new MetaLexicon(data, 0, data.length());
        metaLexicon.find();
        metaLexicon.print();
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

//        MetaLexicon metaLexicon = new MetaLexicon(data, 0, data.length());
//        metaLexicon.find();

        long begin = System.nanoTime();
        MetaLexicon metaLexicon = new MetaLexicon(data, 0, data.length());
        metaLexicon.find();
        long end = System.nanoTime();

        if (metaLexicon.match()) {
            match_n++;
        } else
            System.out.println("Not match!");
        time_n += (end-begin);

//        Parser.parse(data, "");

        begin = System.nanoTime();
        Parser.parse(data, "");
        end = System.nanoTime();

        time_parse += (end-begin);
    }
}