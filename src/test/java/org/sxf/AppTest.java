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
        try {
            data = App.readFile("src/test/data/generated.html");
        } catch(IOException e) {
            e.printStackTrace();
        }

        ParallelParser pp = new ParallelParser(data, 4);
        Document d = pp.parse();
        assertTrue(d != null);
    }
}
