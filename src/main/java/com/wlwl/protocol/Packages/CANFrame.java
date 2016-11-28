package com.wlwl.protocol.Packages;
public class CANFrame {

	private String CANID;
	
	private byte[] data;

	public String getCANID() {
		return CANID;
	}
	public void setCANID(String cANID) {
		CANID = cANID;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
 
}
