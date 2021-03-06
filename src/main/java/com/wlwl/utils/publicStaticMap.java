package com.wlwl.utils;


import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.one.SessionManager;

public class publicStaticMap {
	
	private  publicStaticMap()
	{
		
	}
//	private static Map<String,Map<String, Pair>> values=new ConcurrentHashMap <>();
	
	/**
	 * 
	 */
	private static  Map<String,VehicleInfo >vehicles=new ConcurrentHashMap <>();
	private static BlockingQueue<ProtocolModel> sendQueue = new LinkedBlockingQueue<ProtocolModel>();
	private static BlockingQueue<SourceMessage> cmdQueue = new LinkedBlockingQueue<SourceMessage>();
	private static SessionManager sessionManager=new SessionManager();
	public static SessionManager getSessionManager() {
		return sessionManager;
	}

	public static void setSessionManager(SessionManager sessionManager) {
		publicStaticMap.sessionManager = sessionManager;
	}

	public static BlockingQueue<SourceMessage> getCmdQueue() {
		return cmdQueue;
	}

	public static void setCmdQueue(BlockingQueue<SourceMessage> cmdQueue) {
		publicStaticMap.cmdQueue = cmdQueue;
	}

	public static BlockingQueue<ProtocolModel> getSendQueue() {
		return sendQueue;
	}

	public static void setSendQueue(BlockingQueue<ProtocolModel> sendQueue) {
		publicStaticMap.sendQueue = sendQueue;
	}

	public static Map<String, VehicleInfo> getVehicles() {
		return vehicles;
	}

	public static void setVehicles(Map<String, VehicleInfo> vehicles) {
		publicStaticMap.vehicles = vehicles;
	}
	
	
	
	

}
