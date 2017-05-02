package com.wlwl.one;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.kafka.CommandConsumer;
import com.wlwl.kafka.SendDataTokafka;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;

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
		System.out.println(config.getCmdTopic() + "kafkaServser:" + config.getKafkaServer() + "kakagroup:"
				+ config.getKafkaGroupID());

		// 从终端获取车辆原始数据，并把原始数据存在当前数据队列中 发送收到的数据队列
		BlockingQueue<ProtocolModel> sendQueue = new LinkedBlockingQueue<ProtocolModel>();

		// 从kafka中读取数据，并把数据存储到当前队列中 下发的命令队列
		BlockingQueue<SourceMessage> cmdQueue = new LinkedBlockingQueue<SourceMessage>();

		BlockingQueue<String> cmdLogQueue = new LinkedBlockingQueue<String>();

		// 把客户端连接的session存入队列中 session管理
		SessionManager sessionManager = new SessionManager();
		// 从数据库中获取车辆信息数据，并把数据存储到list列表中，设备列表
		Map<String, VehicleInfo> vehicles = new HashMap<String, VehicleInfo>();
		// 定期获取车辆信息的，定时器
		Timer timer = new Timer();
		timer.schedule(new MyTask(vehicles, config), new Date(), 300000);

		// 把原始数据插入kafka中
		SendDataTokafka sendData = new SendDataTokafka(config, sendQueue);
		sendData.setDaemon(true);
		sendData.start();

		// 消费者--从kafka中读取数据，并把数据存入队列中
		CommandConsumer commandConsumer = new CommandConsumer(config, cmdQueue);
		commandConsumer.run();
		//
		// // 发送命令---从数据队列中获取数据，并把数据发送出去
		SendCommandThread sendCommmandThread = new SendCommandThread(sessionManager, cmdQueue, config);
		sendCommmandThread.setDaemon(true);
		sendCommmandThread.start();

		// 启动3G协议网关

		 ServerMainThread smt = new
		 ServerMainThread(config.getTerminalTCPPort(),ProtocolEnum.P3G,
		 sendQueue, vehicles,sessionManager,config);
		 smt.start();

		Timer timer1 = new Timer();
		timer1.schedule(new CheckSession(sessionManager), new Date(), 5000);
		// 启动808协议网关

		 ServerMainThread smt808 = new ServerMainThread(4440,
		 ProtocolEnum.P808, sendQueue, vehicles,sessionManager,config);
		 smt808.start();

		// Protocol jinlong = new Protocol(5442, new ProtocolMessgeForJinLong(),
		// sendQueue, vehicles);
		 ServerMainThread smtJinLong = new
		 ServerMainThread(5442,ProtocolEnum.PJINLONG, sendQueue,
		 vehicles,sessionManager,config);
		 smtJinLong.start();

		//ServerMainThread smtGuoBiao = new ServerMainThread(20292, ProtocolEnum.GUOBIAO, sendQueue, vehicles,
		//		sessionManager, config);
		//smtGuoBiao.start();
		//ServerMainThread smtGuoBiao01 = new ServerMainThread(20293, ProtocolEnum.GUOBIAO, sendQueue, vehicles,
		//		sessionManager, config);
		//smtGuoBiao01.start();

	}

}
