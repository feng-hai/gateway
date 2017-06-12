package com.wlwl.one;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.model.VehicleInfo;
import com.wlwl.mysql.JdbcUtils;
import com.wlwl.mysql.SingletonJDBC;
import com.wlwl.utils.Config;
import com.wlwl.utils.StrFormat;

public class MyTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(MyTask.class);

	private Map<String, VehicleInfo> vehicles;

	public MyTask(Map<String, VehicleInfo> vis) {
		this.vehicles = vis;

		// loadData();
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

		loadData();

	}

	private void loadData() {
		// 重新加载配置文件中数据
		PropertyResource.getInstance().reLoadProperty();
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		if (config.get("log.level").equals("INFO")) {
			LogManager.getRootLogger().setLevel(Level.INFO);
		} else if (config.get("log.level").equals("WARN")) {
			LogManager.getRootLogger().setLevel(Level.WARN);
		} else if (config.get("log.level").equals("ERROR")) {
			LogManager.getRootLogger().setLevel(Level.ERROR);
		}else if (config.get("log.level").equals("OFF")) {
			LogManager.getRootLogger().setLevel(Level.OFF);
		}
		// 查询数据库
		JdbcUtils jdbcUtils = null;
		try {
			jdbcUtils = SingletonJDBC.getJDBC();
			logger.info("数据库初始化，正在加载数据中...");
			String sql = "select * FROM (select vin,id unid ,GPS_ID device_id FROM emcs.bs_machinery_equipment) dd";
			List<Object> params = new ArrayList<Object>();

			List<VehicleInfo> list = (List<VehicleInfo>) jdbcUtils.findMoreRefResult(sql, params, VehicleInfo.class);
			for (VehicleInfo vi : list) {
//				if (!isContains(vi)) {
//					this.vehicles.put(vi.getDEVICE_ID().trim(), vi);
//				}
//				if (!isContainsForPhone(vi)) {
//					this.vehicles.put(vi.getCELLPHONE().trim(), vi);
//				}
				if (!isContainsForVIN(vi)) {
					this.vehicles.put(StrFormat.addZeroForNum(vi.getVIN().trim(), 17), vi);
				}
			}
			logger.info("数据库加载成功，加载数据的个数为：{}", this.vehicles.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jdbcUtils != null) {
				jdbcUtils.releaseConn();
			}
		}
	}
	private Boolean isContains(VehicleInfo vi) {
		return this.vehicles.containsKey(vi.getDEVICE_ID().trim());
	}

	private Boolean isContainsForPhone(VehicleInfo vi) {
		return this.vehicles.containsKey(vi.getCELLPHONE().trim());
	}

	private Boolean isContainsForVIN(VehicleInfo vi) {
		return this.vehicles.containsKey(StrFormat.addZeroForNum(vi.getVIN().trim(), 17));
	}

}
