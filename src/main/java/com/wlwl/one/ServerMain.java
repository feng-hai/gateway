package com.wlwl.one;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.wlwl.kafka.CommandConsumer;
import com.wlwl.kafka.SendDataTokafka;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.protocol.Protocol;
import com.wlwl.protocol.Packages.ProtocolMessageFor808;
import com.wlwl.protocol.Packages.ProtocolMessgeFor3G;
import com.wlwl.protocol.Packages.ProtocolMessgeForJinLong;
import com.wlwl.utils.Config;
import com.wlwl.utils.SourceMessage;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// 配置文件
		Config config = new Config();

		//从终端获取车辆原始数据，并把原始数据存在当前数据队列中 发送收到的数据队列
		BlockingQueue<ProtocolModel> sendQueue = new LinkedBlockingQueue<ProtocolModel>();

		//从kafka中读取数据，并把数据存储到当前队列中 下发的命令队列
		BlockingQueue<SourceMessage> cmdQueue = new LinkedBlockingQueue<SourceMessage>();

		//把客户端连接的session存入队列中 session管理
		SessionManager sessionManager = new SessionManager();
		//从数据库中获取车辆信息数据，并把数据存储到list列表中，设备列表
		List<VehicleInfo> vehicles=new Vector<VehicleInfo>();
		 //定期获取车辆信息的，定时器
		 Timer timer = new Timer();
	     timer.schedule(new MyTask(vehicles),new Date(),300000);

		// 把原始数据插入kafka中
		SendDataTokafka sendData = new SendDataTokafka(config, sendQueue);
		sendData.setDaemon(true);
		sendData.start();

		// 消费者--从kafka中读取数据，并把数据存入队列中
		//CommandConsumer commandConsumer = new CommandConsumer(config, cmdQueue);
		//commandConsumer.run();
//
//		// 发送命令---从数据队列中获取数据，并把数据发送出去
		SendCommandThread sendCommmandThread = new SendCommandThread(sessionManager, cmdQueue);
		sendCommmandThread.setDaemon(true);
		sendCommmandThread.start();
		
        //启动3G协议网关
		Protocol g3 = new Protocol(18081,new ProtocolMessgeFor3G(),sendQueue,vehicles);
		
		 ServerMainThread smt = new ServerMainThread(g3, g3, sessionManager);
		 smt.start();
		 
		 
		 Timer timer1 = new Timer();
	     timer1.schedule(new CheckSession(sessionManager),new Date(),5000);
		//启动808协议网关
//		My808Protocol g808 = new My808Protocol(9014);
//		ServerMainThread smt808 = new ServerMainThread(g808, g808, sessionManager);
//		smt808.start();
		
//		Protocol g808 = new Protocol(9014,new ProtocolMessageFor808(),sendQueue,vehicles);
//		ServerMainThread smt808 = new ServerMainThread(g808, g808, sessionManager);
//		smt808.start();
	   
			Protocol jinlong = new Protocol(8989,new ProtocolMessgeForJinLong(),sendQueue,vehicles);
			ServerMainThread smtJinLong = new ServerMainThread(jinlong, jinlong, sessionManager);
			smtJinLong.start();
		
		
		
	}

}
