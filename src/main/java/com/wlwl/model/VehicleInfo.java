package com.wlwl.model;

public class VehicleInfo {
	
	private String UNID;
	private String  ROOT_PROTO_UNID;
	public String getROOT_PROTO_UNID() {
		return ROOT_PROTO_UNID;
	}
	public void setROOT_PROTO_UNID(String rOOT_PROTO_UNID) {
		ROOT_PROTO_UNID = rOOT_PROTO_UNID;
	}
	private String DEVICE_ID;
	private String CELLPHONE;
	
	public String getUNID() {
		return UNID;
	}
	public void setUNID(String uNID) {
		UNID = uNID;
	}

	public String getDEVICE_ID() {
		return DEVICE_ID;
	}
	public void setDEVICE_ID(String dEVICE_ID) {
		DEVICE_ID = dEVICE_ID;
	}
	public String getCELLPHONE() {
		return CELLPHONE;
	}
	public void setCELLPHONE(String cELLPHONE) {
		CELLPHONE = cELLPHONE;
	}

	

}
