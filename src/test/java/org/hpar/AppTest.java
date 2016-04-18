package org.hpar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jsoup.nodes.*;
import org.jsoup.parser.Parser;

import java.io.IOException;

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
    public void testApp() throws IOException {

        String data = App.readFile("src/test/extern/index.html");
        Document dd = Parser.parse(data, "");

        Document d = null;
        try {
            ParallelParser pp = new ParallelParser(data, 4);
            d = pp.parse();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        assertNotNull(d);
        System.out.println(dd.tag());
        System.out.println(d.tag());
        assertTrue(d.getClass() == dd.getClass());

        assertTrue(dd.equals(d));
    }
}
