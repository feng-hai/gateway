package gateway.gateway;

import java.util.Date;

import org.apache.log4j.Logger;

import com.wlwl.model.ProtocolModel;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.SourceMessage;

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
    	SourceMessage message=	new SourceMessage(" {\"raw_octets\": \"0102030405060708090A\", \"DEVICE_ID\": \"TEST01\"}");
    	
    	ProtocolModel pm=new ProtocolModel();
    	pm.setCELLPHONE("15895910680");
    	pm.setRAW_OCTETS("0102030405060708090A");
    	pm.setDEVICE_ID("TEST01");
    	pm.setIP4("127.0.0.1");
    	pm.setLength("20");
    	pm.setFlag_transmit("false");
    	pm.setNode_unid("11111111");
    	pm.setProto_unid("222222");
    	pm.setTIMESTAMP(new Date().getTime());
    	pm.setUnid("223343443");
    	
    	System.out.println(pm);
    	
    	logger.debug("开始");
        assertTrue( true );
    }
}
