package com.wlwl.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.one.SendCommandThread;
//import com.wlwl.utils.AychWriter;
import com.wlwl.utils.SourceMessage;
import com.wlwl.utils.publicStaticMap;

public class CommandConsumerThread implements Runnable {

	//private BlockingQueue<SourceMessage> cmdQueue;

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private KafkaConsumer<String, String> consumer;
	private static final Logger logger = LoggerFactory.getLogger(CommandConsumerThread.class);

	public CommandConsumerThread(KafkaConsumer<String, String> consumer) {
		this.consumer = consumer;
	//	this.cmdQueue = cmdQueue;
	}

	public void run() {
		// TODO Auto-generated method stub
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		logger.warn("topicName--：" + config.get("kafka.cmdTopic"));	
		consumer.subscribe(Arrays.asList(config.get("kafka.cmdTopic").trim()));	
		while (!closed.get()) {
			try {
			//	consumer.subscribe(Arrays.asList("octets_down"));	
				ConsumerRecords<String, String> records = consumer.poll(100); 
				for (ConsumerRecord<String, String> record : records) {
					System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(),
							record.value());
					 logger.warn("收到kafka数据--：" + record.value());	
					SourceMessage message = new SourceMessage(record.value());

					//logger.trace(message.toString());
					if (message.getDEVICE_ID() != null) {
						publicStaticMap.getCmdQueue().put(message);
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				try {
					Thread.sleep(1000);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	// Shutdown hook which can be called from a separate thread
	public void shutdown() {
		System.out.println("close consumer thread!");
		closed.set(true);
		consumer.wakeup();
	}

}
