package com.wlwl.thread;

import java.util.Scanner;

import com.wlwl.utils.publicStaticMap;

public class CheckVehicleInfo extends Thread {
	
	
//	private String vId;
//  public CheckVehicleInfo(String vehicleId)
//  {
//	  this.vId=vehicleId;
//  }
  @SuppressWarnings("resource")
	@Override
    public void run() {
		
        while (!Thread.currentThread().isInterrupted()) {   
            try {
            	
            	// 启动监控
				 System.out.println("请输入车辆的VIN码或终端id,输入vc退出监控。");
				
				Scanner sc = new Scanner(System.in);
				 String vehicleID = sc.next();
				 if(vehicleID.equals("vc"))
				 {
					 cancel();
				 }
				 if(publicStaticMap.getVehicles().containsKey(vehicleID))
				 {
					 System.out.println("你要监控的信息："+vehicleID);
					 System.out.println(publicStaticMap.getVehicles().get(vehicleID));
					 Thread.sleep(1000);
				 }else
				 {
					 System.out.println("你要监控的车辆信息："+vehicleID+"不存在"); 
				 }
            } catch (InterruptedException e) {
                System.out.println("interrupt");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("thread stop");
    }

    public void cancel() {
        interrupt();
    }

}
