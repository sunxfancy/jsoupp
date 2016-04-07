package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;

/**
 * for all tests
 * Created by sxf on 4/6/16.
 */
public class AllTest extends TestCase {

    int k = 0;
    int p = 0;
    int all = 0;
    public void testAll() {
        k = 0;
        loadAllFiles("src/test/extern/websites");
        System.out.println("正确率： " + k*100/all + "%");
        System.out.println("崩溃率： " + (all-p)*100/all + "%");
    }

    public void loadAllFiles(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
        assert files != null;
        all = files.length;
        for(File file : files) {
            System.out.println(file.getAbsolutePath());
            try {
                loadOneFile(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadOneFile(String path) throws IOException {
        String data = App.readFile(path);

        long begin = System.nanoTime();
        Document dd = Parser.parse(data, "");
        long end = System.nanoTime();
        System.out.println("normal time: " + (end - begin) / 1000000 + "ms");

        Document d = null;
        try {
            ParallelParser pp = new ParallelParser(data, 4);
            d = pp.parse();
        } catch (Exception e1) {
            e1.printStackTrace();
            throw e1;
        }
        assertNotNull(d);
        assertTrue(d.getClass() == dd.getClass());
        p++;
        if (!dd.equals(d)) {
            k++;
            System.out.println("出现异常！");
        }
    }
}
