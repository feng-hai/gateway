package com.wlwl.protocol.Packages;
import java.util.List;

public class FaultBody {
	
	private short status;
	
	private List<FaultData> list;

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public List<FaultData> getList() {
		return list;
	}

	public void setList(List<FaultData> list) {
		this.list = list;
	}

}
