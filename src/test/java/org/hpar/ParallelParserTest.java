package org.hpar;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;

import java.util.Objects;

public class ParallelParserTest extends TestCase {

    public void testParse() throws Exception {
        Document d = null;
        try {
            String data = App.readFile("src/test/extern/index2.html");
            ParallelParser pp = new ParallelParser(data, 4);
            d = pp.parse();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        assertNotNull(d);
        System.out.println("testParse Done.\n");

    }

    public void testPartition() throws Exception {
        String data = App.readFile("src/test/extern/index.html");
        ParallelParser pp = new ParallelParser(data, 4);
        String[] strings = pp.inputs;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i != 0)
                sb.append(strings[i].substring(6));
            else sb.append(strings[i]);
        }
        assertTrue(Objects.equals(sb.toString(), data));
        System.out.println("testPartition Done.\n");
    }

    public void testMerge() throws Exception {

    }

    public void testGetRightMost() throws Exception {

    }
}