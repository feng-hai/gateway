package com.wlwl.one;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {

	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	private ConcurrentHashMap<String, IoSession> map = new ConcurrentHashMap<String, IoSession>();
	
	public void addSession(String deviceID, IoSession session) {
		try {			
			//if (!map.containsKey(deviceID)) {
				map.put(deviceID, session);
//			}else
//			{
//				if(!map.get(deviceID).isConnected())
//				{
//					map.put(deviceID, session);
//				}
//			}
			if (!session.containsAttribute("ID")) {
				session.setAttribute("ID", deviceID);
			}
		} catch (Exception e) {
			logger.error("addSession exception!" + e.toString());
		}
	}

	public IoSession getSession(String deviceID) {
		return map.get(deviceID);
	}

	public void removeSession(long deviceID, IoSession session) {
		try {
			IoSession currentSession = map.get(deviceID);
			if (currentSession.getId() == session.getId()) {
				map.remove(deviceID);
			}
		} catch (Exception e) {
			logger.error("removeSession exception!" + e.toString());
		}
	}

	public void removeSession(IoSession session) {

		try {
			Object o = session.getAttribute("ID");
			if (o != null && o instanceof String) {
				String ID = (String) o;
				map.remove(ID,session);
			}
		} catch (Exception e) {
			logger.error("removeSession2 exception!" + e.toString());
		}
	}

	public void writeSession(String deviceID, byte[] data) {
		IoSession session = this.getSession(deviceID);
		try {
			if (session != null) {
				session.write(data);
			}
		} catch (Exception e) {
			logger.error("write session exception!" + e.toString());
		}
	}

	public int getCount() {
		return map.size();
	}
}
