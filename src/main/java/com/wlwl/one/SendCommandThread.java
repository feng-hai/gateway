package com.wlwl.one;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.utils.AychWriter;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.Config;
import com.wlwl.utils.SourceMessage;

/*
 * 发送命令至终端设备
 */
public class SendCommandThread extends Thread {

	private SessionManager sessionManager;

	private BlockingQueue<SourceMessage> cmdQueue;
	private Config _config;
	private static final Logger logger = LoggerFactory.getLogger(SendCommandThread.class);

	public SendCommandThread(SessionManager sessionManager, BlockingQueue<SourceMessage> cmdQueue, Config config) {
		this.sessionManager = sessionManager;
		this.cmdQueue = cmdQueue;
		this._config = config;
	}

	public void run() {
		while (true) {
			try {
				SourceMessage message = cmdQueue.take();
				//if (this._config.getWatchVehiclesList().contains(message.getDEVICE_ID())) {
					new AychWriter("发送数据--：" +  "--" +message.getRaw_octets(), "SendMessage").start();	
				//}
				sessionManager.writeSession(message.getDEVICE_ID(),ByteUtils.hexStr2Bytes(message.getRaw_octets()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
