package com.wlwl.kafka;


import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import kafka.utils.VerifiableProperties;


public class PartitionerDemo implements Partitioner {
	public PartitionerDemo(VerifiableProperties props) {

	}

//	public int partition(Object obj, int numPartitions) {
//		int partition = 0;
//		if (obj instanceof String) {
//			String key=(String)obj;
//			int offset = key.lastIndexOf('.');
//			if (offset > 0) {
//				partition = Integer.parseInt(key.substring(offset + 1)) % numPartitions;
//			}
//		}else{
//			partition = obj.toString().length() % numPartitions;
//		}
//		
//		return partition;
//	}
	
	  @Override
	    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
	        // TODO Auto-generated method stub
	        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
	        int numPartitions = partitions.size();
	        int partitionNum = 0;
	        try {
	            partitionNum = Integer.parseInt((String) key);
	        } catch (Exception e) {
	            partitionNum = key.hashCode() ;
	        }
	      // LOG.info("the message sendTo topic:"+ topic+" and the partitionNum:"+ partitionNum);
	        return Math.abs(partitionNum  % numPartitions);
	    }

	

	@Override
	public void configure(Map<String, ?> configs) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}