package com.wlwl.protocol.Packages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

import com.wlwl.utils.MessageConstants;
import com.wlwl.utils.MessageTail;
import com.wlwl.utils.MessageTools; 

 
public class DefaultMessage implements Serializable, Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8581354218243032580L;

	/**
     * 头结构
     */
    private DefaultHeader header;

    /**
     * GPS消息体
     */
    private GPSBody gpsBody;

    /**
     * CAN消息体
     */
    private CANBody canBody;
    
    /*
     * 锁车消息体
     */
    private LockBody lockBody;
    
    /*
     * 报警数据消息体
     */
    private FaultBody faultBody;
    
 

    /**
     * 尾结构，用于5系列CRC校验
     */
    private DefaultEnd end;
    
    /*
     * 发送GPS消息体数据
     */
    private byte[] sendGPSData;
    
    /*
     * 发送CAN消息体数据
     */
    private byte[] sendCANData;    
    
    /*
     * 错误提示，正确解析就为空
     */
    private String errorMsg="";
    
  
	public DefaultHeader getHeader() {
		return header;
	}

	public void setHeader(DefaultHeader header) {
		this.header = header;
	}

	public GPSBody getGpsBody() {
		return gpsBody;
	}

	public void setGpsBody(GPSBody gpsBody) {
		this.gpsBody = gpsBody;
	}

	public DefaultEnd getEnd() {
		return end;
	}

	public void setEnd(DefaultEnd end) {
		this.end = end;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

    /**
     * 附件数据，包括消息接收时间，矫正数据
     */
    //private Append append;
	
	public byte[] getData(){
		int length=0;
		
		
		//获取命令体长度
		short bodyLength=0;
		
        //如果是获取服务器时间请求
		byte[] dataBuffer=null;
        if(header.getGpsCommandId()==MessageConstants.MESSAGE_TYPE_SERVERTIME_RESPONSE){
        	dataBuffer = MessageTools.dateToBytesEx(new Date());
        	length += dataBuffer.length;
        	
        	bodyLength += dataBuffer.length;
        	
            header.setGpsLength(bodyLength);
        }else{
        	if(sendGPSData!=null){
        		length += sendGPSData.length;
        		dataBuffer=sendGPSData;
        		bodyLength += sendGPSData.length;
                header.setGpsLength(bodyLength);
        	}else if(sendCANData!=null){
        		length += sendCANData.length;
        		dataBuffer=sendCANData;
        		bodyLength += sendCANData.length;
                header.setGpsLength(bodyLength);
        	}
        }
		 
        

		
		byte[] hdata=header.getData();
		
		//计算长度
		if(hdata != null){
			length += hdata.length ;
		}
		

 
	    //组装数据，进行CRC检验
        ByteBuffer bf = ByteBuffer.allocate(length);
        if (null != hdata)
        {
            bf.put(hdata);
        }
        
        if (null != dataBuffer)
        {
            bf.put(dataBuffer);
        }
        
        
      
        // CRC 获取包尾
        byte[] endBody = MessageTail.parseCRCMessageTailShort(bf.array());

        if (length > 0)
        {
            ByteBuffer buf = MessageTools.allocate(length + 2);
            if (header != null)
            {
                buf.put(hdata);
            }
            
            //内容
            if (null != dataBuffer)
            {
                buf.put(dataBuffer);
            }
  
            //end
            if (null != endBody)
            {
                buf.put(endBody);
            }
                
            byte[] bodyBytes = buf.array();
            byte[] hbcTrans = MessageTools.enCodeFormat(bodyBytes);
            //转义加头尾标识后的buf
            ByteBuffer tranBuf = ByteBuffer.allocate(2 + hbcTrans.length);
            tranBuf.put(MessageConstants.MESSAGE_FLAG);
            tranBuf.put(hbcTrans);
            tranBuf.put(MessageConstants.MESSAGE_FLAG);

            return tranBuf.array();
        }
        
        return null;
	}
	
    @Override 
    public DefaultMessage clone() throws CloneNotSupportedException {  
    	DefaultMessage defaultMessage = (DefaultMessage) super.clone();  
    	defaultMessage.header  = (DefaultHeader) this.header.clone();  
        return defaultMessage;  
    }

	public CANBody getCanBody() {
		return canBody;
	}

	public void setCanBody(CANBody canBody) {
		this.canBody = canBody;
	}

 

	public void setSendGPSData(byte[] sendData) {
		this.sendGPSData = sendData;
	}  
	
	public void setSendCANData(byte[] sendData) {
		this.sendCANData = sendData;
	}

	public LockBody getLockBody() {
		return lockBody;
	}

	public void setLockBody(LockBody lockBody) {
		this.lockBody = lockBody;
	}

	public FaultBody getFaultBody() {
		return faultBody;
	}

	public void setFaultBody(FaultBody faultBody) {
		this.faultBody = faultBody;
	}

 

	
}
