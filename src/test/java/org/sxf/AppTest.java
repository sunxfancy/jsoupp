package org.sxf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hpar.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.io.*;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        String data = "";
        Document d = null;
        try {
            data = App.readFile("src/test/extern/index.html");

            ParallelParser pp = new ParallelParser(data, 4);
            d = pp.parse();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        assertTrue(d != null);
    }
}
