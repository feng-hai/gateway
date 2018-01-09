package com.wlwl.utils;

import java.io.Serializable;
import java.util.Date;

/*
 * 源码消息体
 */

public class SourceMessage implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	private String raw_octets;

	public String getRaw_octets() {
		return raw_octets;
	}

	public void setRaw_octets(String raw_octets) {
		this.raw_octets = raw_octets;
	}
	
	private String device_ID;

	public String getDEVICE_ID() {
		return device_ID;
	}

	public void setDEVICE_ID(String dEVICE_ID) {
		device_ID = dEVICE_ID;
	}

	public SourceMessage()
	{
		
	}

	/**
	 * @param str   数据格式  {"raw_octets": "0102030405060708090A", "DEVICE_ID": "TEST01"}
	 */
	public SourceMessage(String str) {
		
		str=str.replaceAll("DEVICE_ID", "device_ID");
		SourceMessage temp=JsonUtils.deserialize(str,SourceMessage.class );
		this.device_ID=temp.getDEVICE_ID();
		this.raw_octets=temp.getRaw_octets();
		//String[] strArray = str.split(",");
		// if(strArray!=null && strArray.length==4){
		// gateWayID=strArray[0];
		// DT=strArray[1];
		// address=strArray[2];
	      //data=MessageTools.hexStringToByte(strArray[3]);
		// }
		
	}



	public String toString() {
		StringBuilder sb = new StringBuilder();
		 sb.append(this.device_ID);
		 sb.append(",").append(this.raw_octets);
		//
		// String strData=MessageTools.bytesToString(data);
		// sb.append(",").append(strData);
		return sb.toString();
	}

}
