package com.wlwl.kafka;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.model.ProtocolModel;
import com.wlwl.utils.AychWriter;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class SendDataTokafka extends Thread {

	private BlockingQueue<ProtocolModel> sendQueue;

	private Producer<String, String> producer;

	private static final Logger logger = LoggerFactory.getLogger(SendDataTokafka.class);

	public SendDataTokafka(BlockingQueue<ProtocolModel> queue) {

		this.sendQueue = queue;
		initKafka();

	}

	private void initKafka() {

		// 设置配置属性"ZS0114PDNEV01:9092,ZS0114PDNEV02:9092,ZS0114PDNEV03:9092"
		Properties props = new Properties();
		// props.put("metadata.broker.list","GMSBDDN1:9092,GMSBDDN2:9092,GMSBDDN3:9092");
		props.put("metadata.broker.list", PropertyResource.getInstance().getProperties().get("kafka.server"));
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		// key.serializer.class默认为serializer.class
		props.put("key.serializer.class", "kafka.serializer.StringEncoder");
		// 可选配置，如果不配置，则使用默认的partitioner
		props.put("partitioner.class", "com.wlwl.kafka.PartitionerDemo");
		// 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
		// 值为0,1,-1,可以参考
		// http://kafka.apache.org/08/configuration.html
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);

		// 创建producer
		producer = new Producer<String, String>(config);

	}

	public void run() {
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		while (true) {
			try {
				ProtocolModel message = sendQueue.take();
				String strMessage = message.toString();
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(
						config.get("kafka.sourcecodeTopic"), message.getUnid(), strMessage);
						// System.out.println(ip);

				// if(config.getIsDebug()==1){
				// System.out.println("kafka sending! topic:
				// "+config.getSourcecodeTopic()+" message: "+ strMessage);
				// }

				try {
					Date time = new Date(Long.parseLong(message.getTIMESTAMP()));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					logger.info(sdf.format(time) + message.getDEVICE_ID() + strMessage);
				} catch (Exception ex) {
					logger.error("数据转化：", ex);
				}
				// logger.error(strMessage);
				List<String> watchs = java.util.Arrays.asList(config.get("terminals").split(","));
				if (watchs.contains(message.getDEVICE_ID())) {
					new AychWriter(message.getRAW_OCTETS(), "Octests").start();
				}

				producer.send(data);


			} catch (Exception e) {
			}
		}
	}

	public void shutdown() {
		producer.close();
	}

}
