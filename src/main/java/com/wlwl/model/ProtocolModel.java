package com.wlwl.model;

import java.io.Serializable;

import com.wlwl.utils.JsonUtils;

public class ProtocolModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String DEVICE_ID;//终端编号
	private String RAW_OCTETS;// 	十六进制码流
	private String unid;// 	唯一编号
	private String proto_unid;//协议唯一编号
	private String node_unid;//节点唯一编号
	private String length;// 	长度(Byte)
	private String TIMESTAMP;// 	UNIX时间戳(毫秒)	
	private String cellphone;//电话号码
	private String IP4;//IPv4地址
	private String flag_transmit="false";//转发标志
	
	public String getTIMESTAMP() {
		return TIMESTAMP;
	}
	public String getCELLPHONE() {
		return cellphone;
	}

	public void setCELLPHONE(String cELLPHONE) {
		cellphone = cELLPHONE;
	}

	public void setTIMESTAMP(String tIMESTAMP) {
		TIMESTAMP = tIMESTAMP;
	}

	public String getNode_unid() {
		return node_unid;
	}
	public void setNode_unid(String node_unid) {
		this.node_unid = node_unid;
	}
	public String getDEVICE_ID() {
		return DEVICE_ID;
	}
	public void setDEVICE_ID(String dEVICE_ID) {
		DEVICE_ID = dEVICE_ID;
	}
	public String getRAW_OCTETS() {
		return RAW_OCTETS;
	}
	public void setRAW_OCTETS(String rAW_OCTETS) {
		RAW_OCTETS = rAW_OCTETS;
	}
	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}
	public String getProto_unid() {
		return proto_unid;
	}
	public void setProto_unid(String proto_unid) {
		this.proto_unid = proto_unid;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	
	public String getIP4() {
		return IP4;
	}
	public void setIP4(String iP4) {
		IP4 = iP4;
	}
	public String getFlag_transmit() {
		return flag_transmit;
	}
	public void setFlag_transmit(String flag_transmit) {
		this.flag_transmit = flag_transmit;
	}
	
	public String toString()
	{
		
		return JsonUtils.serialize(this)
				.replace("device_ID", "DEVICE_ID")
				.replace("timestamp", "TIMESTAMP")
				.replace("ip4", "IP4")
				.replace("raw_OCTETS", "raw_octets");
		
	}
	

	

}
