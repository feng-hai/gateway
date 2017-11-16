package com.wlwl.thread;

import java.util.Scanner;

import com.wlwl.utils.publicStaticMap;

public class ReadInputMessage extends Thread {

	private CheckOnlineCount checkOnline;
	private CheckVehicleInfo checkVehicle;
	
	 private Object proceedLock= new Object();  

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
				System.out.println("c:查询在线车辆数");
				System.out.println("cc:查询在线车辆数");
				System.out.println("v:查询车辆信息");
				System.out.println("vc:查询车辆信息");
				break;
			}
			case "c": {
				// 启动监控
				System.out.println("启动监控在线车辆数线程：开始");
				if (checkOnline == null) {
					checkOnline = new CheckOnlineCount();
					checkOnline.start();
				} else {
					System.out.println("监控车辆在线数线程：已经启动");
					break;
				}
				System.out.println("启动监控在线车辆数线程：成功");
				break;
			}
			case "cc": {
				// 启动监控
				System.out.println("关闭监控在线车辆数线程：开始");
				if (checkOnline != null) {
					checkOnline.cancel();
					checkOnline = null;
				} else {
					System.out.println("监控在线车辆数线程：没有启动！");
					break;
				}

				System.out.println("关闭监控在线车辆数线程：成功");
				break;
			}
			case "v": {
				// 启动监控
				//System.out.println("请输入车辆的VIN码或终端id");
				//String vehicleID = sc.next();
				//if (publicStaticMap.getVehicles().containsKey(vehicleID)) {
					System.out.println("启动监控车辆数线程：开始");
					if (checkVehicle == null) {
						checkVehicle = new CheckVehicleInfo(proceedLock);
						checkVehicle.start();
						try {
							 synchronized ( proceedLock ) { 
							  proceedLock.wait();
							
							 }
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("启动监控车辆数线程：成功");
					} else {
						System.out.println("启动监控车辆数线程：已经启动车辆监控线程");
					}
				//}else
//					{
//						System.out.println("你要监控的车辆："+vehicleID+"在内存中不存在");
//					}
				break;
			}
			case "vc": {
				// 启动监控
				if (checkVehicle != null) {
					checkVehicle.cancel();
					checkVehicle = null;
					System.out.println("关闭监控车辆数线程：成功");
				} else {
					System.out.println("没有启动车辆信息监控线程，无法关闭");
				}
				break;
			}
			default:
				System.out.println("你输入的内容不正确，请重新输入：");
				System.out.println("c:查询在线车辆数");
				System.out.println("cc:查询在线车辆数");
				System.out.println("v:查询车辆信息");
				System.out.println("vc:查询车辆信息");
				break;
			}
		}
	}

}
