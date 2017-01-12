package com.wlwl.one;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.wlwl.model.VehicleInfo;
import com.wlwl.mysql.JdbcUtils;
import com.wlwl.mysql.SingletonJDBC;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

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
