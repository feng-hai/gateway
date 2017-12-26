package com.wlwl.one;

import java.util.Scanner;

import com.wlwl.model.VehicleInfo;
import com.wlwl.utils.JsonUtils;
import com.wlwl.utils.publicStaticMap;

public class ReadInputMessage extends Thread {
	private SessionManager manager;

	public ReadInputMessage(SessionManager _manager) {
		this.manager = _manager;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		while (true) {
			// 读取输入内容
			Scanner sc = new Scanner(System.in);
			System.out.println("请输入你要监控的内容，如需帮助请输入help：");
			String inputStr = sc.next();
			switch (inputStr) {
			case "help": {
				System.out.println("remove:清空缓存");
				System.out.println("get:获取终端id是否存在！");
				break;
			}
			case "remove": {
				System.out.println("开始清空");
				this.manager.remove("211005");
				System.out.println("清空完毕");
				break;
			}
			case "get": {
				Boolean isTrue = this.manager.getDevice("211005");
				if (isTrue) {
					System.out.println("已存在" + this.manager.getSession("211005").getId()
							+ this.manager.getSession("211005").isConnected() + ":"
							+ this.manager.getSession("211005").isClosing());
				} else {
					System.out.println("不存在");
				}
				break;
			}
			case "vehicle": {
				Scanner sc1 = new Scanner(System.in);
				System.out.println("请输入车辆VIN号或终端ID：");
				String inputVehicle = sc1.next();
				VehicleInfo vi = publicStaticMap.getVehicles().get(inputVehicle);
				if (vi != null) {
					System.out.println(JsonUtils.serialize(vi));
				} else {
					System.out.println("平台不存在车辆" + inputVehicle);
				}
				break;
			}
			default:
				System.out.println("remove:清空缓存");
				System.out.println("get:获取终端id是否存在！");
				break;
			}
		}
	}

}
