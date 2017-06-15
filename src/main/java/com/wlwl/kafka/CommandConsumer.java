package com.wlwl.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.utils.SourceMessage;





public class CommandConsumer {

	//private BlockingQueue<SourceMessage> cmdQueue;

	KafkaConsumer<String, String> consumer;

	private static final Logger logger = LoggerFactory.getLogger(CommandConsumer.class);

	private ExecutorService executor;

	private CommandConsumerThread thread;

	public CommandConsumer( ) {
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		//this.cmdQueue = cmdQueue;
		Properties props = new Properties();
		props.put("bootstrap.servers",config.get("kafka.server"));// "maria.cube:9092,namenode.cube:9092,datanode1.cube:9092,hyperrouter1.cube:9092,hyperrouter2.cube:9092"
		props.put("group.id", config.get("kafka.groupID"));
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<String, String>(props);
		executor = Executors.newFixedThreadPool(5);
	}

	public void run() {
		
		thread = new CommandConsumerThread(consumer);
		executor.submit(thread);
	}

	public void shutdown() {

		if (executor != null) {
			try {
				thread.shutdown();
				executor.shutdown();
			} catch (Exception e) {
				logger.error("executor  shutdown exception!" + e.toString());
			}
		}
		try {
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				logger.error("Timed out waiting for consumer threads to shut down, exiting uncleanly");
			}
		} catch (InterruptedException e) {
			logger.error("Interrupted during shutdown, exiting uncleanly");
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
