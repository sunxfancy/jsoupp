package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ProfileTest extends TestCase {

    public void testApp() throws IOException {
        String data = App.readFile("src/test/extern/index.html");
        Document d = null;
        try {
            ParallelParser pp = new ParallelParser(data, 4);
            d = pp.parse();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}