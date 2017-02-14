package com.wlwl.one;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.model.VehicleInfo;
import com.wlwl.mysql.JdbcUtils;
import com.wlwl.mysql.SingletonJDBC;
import com.wlwl.utils.Config;

public class MyTask extends TimerTask {
	

    private static final Logger logger = LoggerFactory.getLogger(MyTask.class);  



	private static Map<String,VehicleInfo> vehicles;

	private Config _config;

	public MyTask(Map<String,VehicleInfo> vis, Config config) {
		this.vehicles = vis;
		
		
		
		this._config = config;
	}
	// public static List<String> getList() {
	// List<String> temp = new ArrayList();
	// try {
	// Workbook wb;
	// wb = Workbook.getWorkbook(new File("D://myFile.xls"));
	// Sheet s = wb.getSheet("车辆绑定信息");
	//
	// for (int i = 1; i < 5111; i++) {
	// Cell c = s.getCell(0, i);
	// String deviceId = c.getContents().trim();
	// if (!temp.contains(deviceId)) {
	// temp.add(deviceId);
	// }
	// }
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (BiffException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return temp;
	// }

	@Override
	public void run() {
		
		this._config.loadMessage();
		// 查询数据库
		JdbcUtils jdbcUtils = null;
		try {
			jdbcUtils = SingletonJDBC.getJDBC(this._config);
			logger.info("数据库初始化成功，正在加载数据中...");
			String sql = "select vi.unid ,device.device_id ,device.cellphone ,pro.root_proto_unid "
					+ " from cube.BIG_VEHICLE vi "
					+ " inner join cube.BIG_DEVICE_VEHICLE_MAP map on vi.unid=map.vehicle_unid "
					+ " inner join cube.BIG_DEVICE device on device .unid=map.device_unid  "
					+ " inner join cube.BIG_FIBER  pro on vi.fiber_unid =pro.unid";
			List<Object> params = new ArrayList<Object>();
			
			List<VehicleInfo> list= (List<VehicleInfo>) jdbcUtils.findMoreRefResult(sql, params, VehicleInfo.class);
			for (VehicleInfo vi : list) {
				if (!isContains(vi)) {
					this.vehicles.put(vi.getDEVICE_ID(), vi);
				}
			}
			logger.info("数据库加载成功，加载数据的个数为：{}",this.vehicles.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jdbcUtils != null) {
				jdbcUtils.releaseConn();
			}
		}

	}

	private Boolean isContains(VehicleInfo vi) {
		for (VehicleInfo vei : this.vehicles.values()) {
			if (vei.getDEVICE_ID().equals(vi.getDEVICE_ID())) {
				return true;
			}
		}
		return false;
	}

}
