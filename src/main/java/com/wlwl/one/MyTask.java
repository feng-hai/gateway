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

public class MyTask extends TimerTask {

	private static List<VehicleInfo> vehicles;

	public MyTask(List<VehicleInfo> vis) {
		this.vehicles = vis;
	}
//	public static List<String> getList() {
//		List<String> temp = new ArrayList();
//		try {
//			Workbook wb;
//			wb = Workbook.getWorkbook(new File("D://myFile.xls"));
//			Sheet s = wb.getSheet("车辆绑定信息");
//
//			for (int i = 1; i < 5111; i++) {
//				Cell c = s.getCell(0, i);
//				String deviceId = c.getContents().trim();
//				if (!temp.contains(deviceId)) {
//					temp.add(deviceId);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (BiffException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return temp;
//	}

	@Override
	public void run() {
		// 查询数据库
		JdbcUtils jdbcUtils = SingletonJDBC.getJDBC();
		String sql = "select vi.unid ,device.device_id ,device.cellphone ,pro.root_proto_unid "
		+ " from cube.BIG_VEHICLE vi "
		+ " inner join cube.BIG_DEVICE_VEHICLE_MAP map on vi.unid=map.vehicle_unid "
		+ " inner join cube.BIG_DEVICE device on device .unid=map.device_unid  "
		+ " inner join cube.BIG_FIBER  pro on vi.fiber_unid =pro.unid";
		List<Object> params = new ArrayList<Object>();
		List<VehicleInfo> list = new ArrayList();
		try {
			list = (List<VehicleInfo>) jdbcUtils.findMoreRefResult(sql, params, VehicleInfo.class);
			jdbcUtils.releaseConn();
			for (VehicleInfo vi : list) {
				if (!isContains(vi)) {
					this.vehicles.add(vi);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Boolean isContains(VehicleInfo vi) {
		for (VehicleInfo vei : this.vehicles) {
			if (vei.getUNID().equals(vi.getUNID())) {
				return true;
			}
		}
		return false;
	}

}
