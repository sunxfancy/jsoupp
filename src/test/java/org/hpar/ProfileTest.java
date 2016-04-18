package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;

public class ProfileTest extends TestCase {

    double time_p, time_n;

    public void testAll() {
        loadAllFiles("src/test/extern/websites");
        System.out.println("Speed Up:"+time_n/time_p+"x");
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
    }

    public void forOneFile(String path) throws IOException {
        String data = App.readFile(path);
        long begin = 0, end;
        Document dd = null;
        for (int i = 0; i < 210; i++) {
            if (i == 10) begin = System.nanoTime();
            dd = Parser.parse(data, "");
        }
        end = System.nanoTime();

        System.out.println("normal time: " + (end - begin) / 1000000 + "ms");
        time_n += (end-begin);
        Document d = null;
        for (int i = 0; i < 210; i++) {
            if (i == 10) begin = System.nanoTime();
            try {
                ParallelParser pp = new ParallelParser(data, 4);
                d = pp.parse();
            } catch (Exception e1) {
                break;
            }
        }
        end = System.nanoTime();
        System.out.println("parallel time: " + (end - begin) / 1000000 + "ms");
        time_p += (end-begin);
    }
}