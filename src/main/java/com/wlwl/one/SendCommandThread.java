package com.wlwl.one;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.utils.AychWriter;
import com.wlwl.utils.ByteUtils;

import com.wlwl.utils.SourceMessage;
import com.wlwl.utils.publicStaticMap;

/*
 * 发送命令至终端设备
 */
public class SendCommandThread extends Thread {

	private SessionManager sessionManager;

	//private BlockingQueue<SourceMessage> cmdQueue;

	private static final Logger logger = LoggerFactory.getLogger(SendCommandThread.class);

	public SendCommandThread(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
		//this.cmdQueue = cmdQueue;
		
		
	
	}
	public int i=0;

	public void run() {
		while (true) {
			try {
				SourceMessage message = publicStaticMap.getCmdQueue().take();
				//if (this._config.getWatchVehiclesList().contains(message.getDEVICE_ID())) {
				
					
					new AychWriter("开始发送数据--：" + message.getDEVICE_ID()+ "--" +message.getRaw_octets(), "SendMessage").start();	
				//}
					if(message.getDEVICE_ID()=="211005")
					{
						if(i++%5==0)
						{	
						}else
						{
					      continue;
						}
					}
				sessionManager.writeSession(message.getDEVICE_ID(),ByteUtils.hexStr2Bytes(message.getRaw_octets()));
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
