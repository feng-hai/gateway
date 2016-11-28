package com.wlwl.kafka;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.model.ProtocolModel;
import com.wlwl.utils.Config;
import com.wlwl.utils.SourceMessage;



 

public class SendDataTokafka extends Thread{
	
	private BlockingQueue<ProtocolModel> sendQueue; 
	
    private Producer<String, String> producer;
 
    
    private Config config;
	
    private static final Logger logger = LoggerFactory.getLogger(SendDataTokafka.class);
    
	public SendDataTokafka(Config config,BlockingQueue<ProtocolModel> queue){
		this.config=config;
		this.sendQueue=queue;
		Properties props = new Properties();
		props.put("bootstrap.servers", config.getKafkaServer());
		props.put("acks", "1");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    producer = new KafkaProducer<String, String>(props);
 
	}
 
	public void run() {		
		while (true) {
			try {
				ProtocolModel message= sendQueue.take();
				String strMessage=message.toString();
				ProducerRecord<String, String> myrecord = new ProducerRecord<String, String>(config.getSourcecodeTopic(), strMessage);
				
				if(config.getIsDebug()==1){
					System.out.println("kafka sending! topic: "+config.getSourcecodeTopic()+" message: "+ strMessage);	
				}
				
				producer.send(myrecord, new Callback() {
					
					public void onCompletion(RecordMetadata metadata, Exception e) {
						if (e != null){
							logger.error(e.toString());
						}
						if(config.getIsDebug()==1){
							
							System.out.println("The offset of the record we just sent is: " + metadata.offset() + "," + metadata.topic());
						    logger.info("The offset of the record we just sent is: " + metadata.offset() + "," + metadata.topic());
						}
					}
				});
             
			} catch (Exception e) {
			}
		}
	}
	
	public void shutdown(){
		producer.close();
	}

 

}
