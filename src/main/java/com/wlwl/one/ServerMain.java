package com.wlwl.one;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;
import java.util.Timer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.PropertyConfigurator;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.kafka.CommandConsumer;
import com.wlwl.kafka.SendDataTokafka;
import com.wlwl.kafka.SendDataTokafkaForGB;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.utils.SourceMessage;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// 配置文件
		// Config config = new Config();
		// System.out.println(config.getCmdTopic() + "kafkaServser:" +
		// config.getKafkaServer() + "kakagroup:"
		// + config.getKafkaGroupID());
		// HashMap<String, String> config =
		// PropertyResource.getInstance().getProperties();
		try {
			String path = new File(".").getCanonicalPath() + "/resource/log4j.properties";
			PropertyConfigurator.configure(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 从终端获取车辆原始数据，并把原始数据存在当前数据队列中 发送收到的数据队列
		// BlockingQueue<ProtocolModel> sendQueue = new
		// LinkedBlockingQueue<ProtocolModel>();

		// 从kafka中读取数据，并把数据存储到当前队列中 下发的命令队列
		// BlockingQueue<SourceMessage> cmdQueue = new
		// LinkedBlockingQueue<SourceMessage>();

		BlockingQueue<String> cmdLogQueue = new LinkedBlockingQueue<String>();

		// 把客户端连接的session存入队列中 session管理
		SessionManager sessionManager = new SessionManager();
		// 从数据库中获取车辆信息数据，并把数据存储到list列表中，设备列表
		// Map<String, VehicleInfo> vehicles = new HashMap<String,
		// VehicleInfo>();
		// 定期获取车辆信息的，定时器
		Timer timer = new Timer();
		timer.schedule(new MyTask(), new Date(), 300000);

		// 把原始数据插入kafka中
		SendDataTokafka sendData = new SendDataTokafka();
		sendData.setDaemon(true);
		sendData.start();
		//把国标数据添加到另外一个topic
		SendDataTokafkaForGB sendDatagb = new SendDataTokafkaForGB();
		sendDatagb.setDaemon(true);
		sendDatagb.start();

		// 消费者--从kafka中读取数据，并把数据存入队列中
		CommandConsumer commandConsumer = new CommandConsumer();
		commandConsumer.run();
		//
		// // 发送命令---从数据队列中获取数据，并把数据发送出去
		SendCommandThread sendCommmandThread = new SendCommandThread(sessionManager);
		sendCommmandThread.setDaemon(true);
		sendCommmandThread.start();

		// 启动3G协议网关

//		ServerMainThread smt = new ServerMainThread(20291, ProtocolEnum.P3G, sessionManager);
//		smt.setDaemon(true);
//		smt.start();

		Timer timer1 = new Timer();
		timer1.schedule(new CheckSession(sessionManager), new Date(), 5000);
		// 启动808协议网关
//
//		ServerMainThread smt808 = new ServerMainThread(4440, ProtocolEnum.P808, sessionManager);
//		smt808.setDaemon(true);
//		smt808.start();

		// Protocol jinlong = new Protocol(5442, new ProtocolMessgeForJinLong(),
		// sendQueue, vehicles);
//		ServerMainThread smtJinLong = new ServerMainThread(5442, ProtocolEnum.PJINLONG, sessionManager);
//		smtJinLong.start();

		ServerMainThread smtGuoBiao = new ServerMainThread(20292, ProtocolEnum.GUOBIAO, sessionManager);
		smtGuoBiao.setDaemon(true);
		smtGuoBiao.start();
//		ServerMainThread smtGuoBiao01 = new ServerMainThread(20293, ProtocolEnum.GUOBIAO, sessionManager);
//		smtGuoBiao01.setDaemon(true);
//		smtGuoBiao01.start();

	}

}
