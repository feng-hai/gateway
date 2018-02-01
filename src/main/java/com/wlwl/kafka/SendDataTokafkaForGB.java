package com.wlwl.kafka;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.model.ProtocolModel;

import com.wlwl.utils.publicStaticMap;

public class SendDataTokafkaForGB extends Thread {

	// private BlockingQueue<ProtocolModel> sendQueue;

	private Producer<String, String> producer;

	private static final Logger logger = LoggerFactory.getLogger(SendDataTokafkaForGB.class);

	public SendDataTokafkaForGB() {

		// this.sendQueue = queue;
		initKafka();

	}

	private void initKafka() {

		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		Properties props = new Properties();
		props.put("bootstrap.servers", config.get("kafka.server"));
		props.put("acks", "1");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		// 可选配置，如果不配置，则使用默认的partitioner
		// props.put("partitioner.class", "com.wlwl.kafka.PartitionerDemo");
		producer = new KafkaProducer<String, String>(props);

	}

	public void run() {
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
	String topicName=	config.get("kafka.sourcecodeTopicForGB");
		while (true) {
			try {
				ProtocolModel message = publicStaticMap.getSendGBQueue().take();
				String strMessage = message.toString();

				if (strMessage.isEmpty()) {
					continue;
				}
				ProducerRecord<String, String> myrecord = new ProducerRecord<String, String>(
						topicName, message.getUnid(), strMessage);

				// if(config.getIsDebug()==1){
				// System.out.println("kafka sending! topic:
				// "+config.getSourcecodeTopic()+" message: "+ strMessage);
				// }

//				try {
//					Date time = new Date(Long.parseLong(message.getTIMESTAMP()));
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					logger.info(sdf.format(time) + message.getDEVICE_ID() + strMessage);
//				} catch (Exception ex) {
//					logger.error("数据转化：", ex);
//					continue;
//				}
				// logger.error(strMessage);
//				List<String> watchs = java.util.Arrays.asList(config.get("terminals").split(","));
//				if (watchs.contains(message.getDEVICE_ID())) {
//					logger.warn(message.getRAW_OCTETS());
//				}

				producer.send(myrecord, new Callback() {

					public void onCompletion(RecordMetadata metadata, Exception e) {
						if (e != null) {
							logger.error("生成者失败",e);
							logger.error("The offset of the record we just sent GB is: " + metadata.offset() + ","
									+ metadata.topic());
						}

						

					}
				});

			} catch (Exception e) {
				logger.error("kafka存储错误", e);
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 initKafka();
			}
		}
	}

	public void shutdown() {
		producer.close();
	}

}
