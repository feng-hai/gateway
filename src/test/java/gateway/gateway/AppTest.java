package gateway.gateway;

import org.apache.log4j.Logger;

import com.wlwl.utils.ByteUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	
	static Logger logger = Logger.getLogger(AppTest.class);
  
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
    	logger.debug("开始");
    	logger.error("dd");
    	
    	logger.error(ByteUtils.getShort(ByteUtils.hexStr2Bytes("E702"), 0) == (short) 0x02E7);
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	
    	logger.debug("开始");
        assertTrue( true );
    }
}
