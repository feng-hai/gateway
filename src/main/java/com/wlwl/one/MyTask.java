package com.wlwl.one;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.model.VehicleInfo;
import com.wlwl.mysql.JdbcUtils;

import com.wlwl.utils.StrFormat;
import com.wlwl.utils.publicStaticMap;

public class MyTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(MyTask.class);

	public MyTask() {
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
		//PropertyResource.getInstance().reLoadProperty();
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		if (config.get("log.level").equals("INFO")) {
			LogManager.getRootLogger().setLevel(Level.INFO);
		} else if (config.get("log.level").equals("WARN")) {
			LogManager.getRootLogger().setLevel(Level.WARN);
		} else if (config.get("log.level").equals("ERROR")) {
			LogManager.getRootLogger().setLevel(Level.ERROR);
		} else if (config.get("log.level").equals("OFF")) {
			LogManager.getRootLogger().setLevel(Level.OFF);
		} else if (config.get("log.level").equals("DEBUG")) {
			LogManager.getRootLogger().setLevel(Level.DEBUG);
		} else if (config.get("log.level").equals("TRACE")) {
			LogManager.getRootLogger().setLevel(Level.TRACE);
		}
		// 查询数据库
		JdbcUtils jdbcUtils = null;
		try {
			jdbcUtils = new JdbcUtils();
			
			logger.info("数据库初始化，正在加载数据中...");
			String sql = "select vi.vin,vi.unid ,device.device_id ,device.cellphone ,pro.root_proto_unid ,device.ICCID,vi.fiber_unid"
					+ " from cube.BIG_VEHICLE vi "
					+ " inner join cube.BIG_DEVICE_VEHICLE_MAP map on vi.unid=map.vehicle_unid   and vi.flag_del=0 and map.flag_ava=1"
					+ " inner join cube.BIG_DEVICE device on device .unid=map.device_unid and device.flag_del=0"
					+ " inner join cube.BIG_FIBER  pro on vi.fiber_unid =pro.unid and pro.flag_del=0";
			List<Object> params = new ArrayList<Object>();
			List<VehicleInfo> list = (List<VehicleInfo>) jdbcUtils.findMoreRefResult(sql, params, VehicleInfo.class);
			Map<String, VehicleInfo> vehicles = new ConcurrentHashMap<>();
			System.out.println("加载的车辆数据："+list.size());
			for (VehicleInfo vi : list) {
				if (!isContains(vi, vehicles)) {
					//if(vi.getDEVICE_ID().equals("25678"))
					//System.out.println(StrFormat.addZeroForNumPre(vi.getDEVICE_ID().trim(), 8));
					vehicles.put(vi.getDEVICE_ID().trim(), vi); 
					vehicles.put(StrFormat.addZeroForNumPre(vi.getDEVICE_ID().trim(), 8), vi); 
				}
				if (!isContainsForPhone(vi, vehicles)) {
					vehicles.put(vi.getCELLPHONE().trim(), vi);
				}
				if (!isContainsForVIN(vi, vehicles)) {
					vehicles.put(StrFormat.addZeroForNum(vi.getVIN().trim(), 17), vi);
				}
			}
			if (vehicles.size() > 0) {
				publicStaticMap.setVehicles(vehicles);
			}
			logger.info("数据库加载成功，加载数据的个数为：{}", publicStaticMap.getVehicles().size() / 3);
		} catch (Exception e) {
			logger.error("数据库访问错误！", e);
		} finally {
		}
	}

	private Boolean isContains(VehicleInfo vi, Map<String, VehicleInfo> vehicles) {
		if (vi.getDEVICE_ID() != null || !vi.getDEVICE_ID().isEmpty()) {
			return vehicles.containsKey(vi.getDEVICE_ID().trim());
		} else {
			return true;
		}
	}

	private Boolean isContainsForPhone(VehicleInfo vi, Map<String, VehicleInfo> vehicles) {
		if (vi.getCELLPHONE() != null || !vi.getCELLPHONE().isEmpty()) {
			return vehicles.containsKey(vi.getCELLPHONE().trim());
		} else {
			return true;
		}
	}

	private Boolean isContainsForVIN(VehicleInfo vi, Map<String, VehicleInfo> vehicles) {
		if (vi.getVIN() != null || !vi.getVIN().isEmpty()) {
			return vehicles.containsKey(StrFormat.addZeroForNum(vi.getVIN().trim(), 17));
		} else {
			return true;
		}
	}

}
