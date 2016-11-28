package com.wlwl.one;

import java.util.concurrent.BlockingQueue;

import com.wlwl.utils.SourceMessage;


/*
 * 发送命令至终端设备
 */
public class SendCommandThread extends Thread{
	
	private SessionManager sessionManager;
	
	private BlockingQueue<SourceMessage> cmdQueue;
	
	public SendCommandThread(SessionManager sessionManager,BlockingQueue<SourceMessage> cmdQueue){
		this.sessionManager=sessionManager;
		this.cmdQueue=cmdQueue;
	}
	
	public void run() {			
		while (true) {			
			try {
				SourceMessage message=cmdQueue.take();
				sessionManager.writeSession(message.getDeviceID(),message.getData());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

 

}
