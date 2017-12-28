package com.wlwl.one;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyTaskSet extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(MyTaskSet.class);

	

	private SessionManager manager;

	public MyTaskSet(SessionManager _manager) {
		this.manager = _manager;
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

		this.manager.remove("211005");

	}

	

}
