package com.wlwl.one;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.model.VehicleInfo;
import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.protocol.ProtocolFactory;
import com.wlwl.utils.AychWriter;

public class SessionManager {

	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	private ConcurrentHashMap<String, IoSession> map = new ConcurrentHashMap<String, IoSession>();

	public void addSession(String deviceID, IoSession session) {
		try {
			if (!session.containsAttribute("ID")) {
				session.setAttribute("ID", deviceID);
			}
			if (map.containsKey(deviceID)) {
				IoSession iSession = map.get(deviceID);
				if (iSession.getId() != session.getId()) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					logger.warn("重复连接关闭老的链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session);
				    iSession.setAttribute("old");
					//iSession.close(true);
				}
			}
			map.put(deviceID, session);

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
				map.remove(ID, session);
			}
		} catch (Exception e) {
			logger.error("removeSession2 exception!" + e.toString());
		}
	}

	public void writeSession(String deviceID, byte[] data) {
		IoSession session = this.getSession(deviceID);
		try {
			if (session != null) {

				ProtocolEnum pEnum=(ProtocolEnum)session.getAttribute("pEnum");
				VehicleInfo vehicle=(VehicleInfo)session.getAttribute("vehicleObject");
				IProtocolAnalysis analysis=ProtocolFactory.getAnalysis(pEnum);
				session.write(analysis.sendBefore(data,vehicle));
			}
		} catch (Exception e) {
			logger.error("write session exception!" + e.toString());
		}
	}

	public int getCount() {

		return map.size();
	}

	public void getDevices() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, IoSession> entry : map.entrySet()) {
			String time = entry.getValue().getAttribute("time").toString();

			// System.out.println("Key = " + entry.getKey() + ", Value = " +
			// entry.getValue());
			sb.append(entry.getKey() + ":" + time);
			sb.append(",");
		}
		logger.error(sb.toString());

	}
}
