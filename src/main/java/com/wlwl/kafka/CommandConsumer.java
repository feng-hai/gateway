package com.wlwl.kafka;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.utils.SourceMessage;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;






public class CommandConsumer {

	private BlockingQueue<SourceMessage> cmdQueue;

	private final ConsumerConnector consumer;

	private static final Logger logger = LoggerFactory.getLogger(CommandConsumer.class);

	private ExecutorService executor;

	private CommandConsumerThread thread;

	public CommandConsumer( BlockingQueue<SourceMessage> cmdQueue) {
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		this.cmdQueue = cmdQueue;
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(config.get("kafka.server"),config.get("kafka.groupID")));
	
	}
	
	private static ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);
	}

	public void run() {
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(config.get("kafka.sourcecodeTopic"), new Integer(config.get("kafka.client.threadcount")));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
				.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(config.get("kafka.sourcecodeTopic"));

		// now launch all the threads
		executor = Executors.newFixedThreadPool(new Integer(config.get("kafka.client.threadcount")));

		for (final KafkaStream stream : streams) {
	        CommandConsumerThread thread = new CommandConsumerThread(stream, cmdQueue);
			executor.submit(thread);
		}
		
		
	}

	
}
