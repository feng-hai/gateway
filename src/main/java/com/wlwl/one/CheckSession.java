package com.wlwl.one;

import java.util.TimerTask;

public class CheckSession extends TimerTask {

	private static SessionManager _sesionM;

	public CheckSession(SessionManager sessionM) {
		this._sesionM = sessionM;
		// List<String> temp=getList();
		//
		// for(String str : temp)
		// {
		// VehicleInfo vi =new VehicleInfo();
		// vi.setCELLPHONE("15895910680");
		// vi.setDEVICE_ID(str);
		// vi.setROOT_PROTO_UNID("111111111");
		// vi.setUNID("11111111222");
		// this.vehicles.add(vi);
		// }
	}

	
	@Override
	public void run() {
		// 查询数据库
		System.out.println(_sesionM.getCount());
	}


}
