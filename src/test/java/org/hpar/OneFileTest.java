package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;

/**
 * Created by sxf on 4/7/16.
 */
public class OneFileTest extends TestCase {

    public void testFile3() {
        loadOneFile("src/test/extern/websites/IEEE Xplore - About IEEE Xplore.html");
    }

    private void loadOneFile(String path){
        String data = null;
        try {
            data = App.readFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        assertTrue(dd.equals(d));
    }
}
