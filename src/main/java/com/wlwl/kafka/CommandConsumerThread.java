package com.wlwl.kafka;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.utils.SourceMessage;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class CommandConsumerThread implements Runnable {

	private BlockingQueue<SourceMessage> cmdQueue;

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private KafkaStream m_stream;
	private static final Logger logger = LoggerFactory.getLogger(CommandConsumerThread.class);

	public CommandConsumerThread(KafkaStream stream, BlockingQueue<SourceMessage> cmdQueue) {
		this.m_stream = stream;
		this.cmdQueue = cmdQueue;
	}

	public void run() {
		// TODO Auto-generated method stub
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();

		while (it.hasNext()) {
			try {
				String temp = new String(it.next().message());
				SourceMessage message = new SourceMessage(temp);
				if (message.getDEVICE_ID() != null) {
					cmdQueue.put(message);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("存入kafka出错", e);

				try {
					Thread.sleep(1000);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		// while (!closed.get()) {
		// try {
		// consumer.subscribe(Arrays.asList("octets_down"));
		// ConsumerRecords<String, String> records = consumer.poll(100);
		// for (ConsumerRecord<String, String> record : records) {
		// System.out.printf("offset = %d, key = %s, value = %s \n",
		// record.offset(), record.key(),
		// record.value());
		// new AychWriter("收到kafka数据--：" + record.value(),
		// "KafKaMessage").start();
		// SourceMessage message = new SourceMessage(record.value());
		//
		// //logger.trace(message.toString());
		// if (message.getDEVICE_ID() != null) {
		// cmdQueue.put(message);
		// }
		//
		// }
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		//
		// try {
		// Thread.sleep(1000);
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
		// }

	}

	// Shutdown hook which can be called from a separate thread

}
