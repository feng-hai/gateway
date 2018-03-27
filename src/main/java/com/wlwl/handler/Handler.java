package com.wlwl.handler;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;

import com.wlwl.one.ServerHandler;
import com.wlwl.one.SessionManager;
import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.protocol.ProtocolFactory;
import com.wlwl.utils.AychWriter;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.Config;
import com.wlwl.utils.JsonUtils;
import com.wlwl.utils.publicStaticMap;

public class Handler {
	// private IServerHandler handler;
	private SessionManager manager;

	private Object message;
	private IoSession session;
	private ProtocolEnum pEnum;
	// private BlockingQueue<ProtocolModel> _sendQueue;
	// private Map<String, VehicleInfo> _vehicles;
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	public Handler(ProtocolEnum pEnum, SessionManager _manager, Object message, IoSession session) {
		// this.handler = _handler;
		this.pEnum = pEnum;
		// this._sendQueue=sendQueue;
		// this._vehicles=vehicles;
		this.manager = _manager;

		this.message = message;
		this.session = session;
	}

	public void excute() {
		// List<String> watchs = this._config.getWatchVehiclesList();

		IProtocolAnalysis analysis = ProtocolFactory.getAnalysis(pEnum);
		byte[] data;
		synchronized (this) {
			String deviceId;

			// 解析类赋值
			if (message instanceof byte[]) {
				data = (byte[]) message;
				if (data == null || data.length < analysis.getMinLength()) {

					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					logger.error("数据异常：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr(data));

					return;
				}
				analysis.setMsg(data);
				deviceId = analysis.getDeviceId();
				if(deviceId.equals("w12345")||deviceId.equals("181111") )
				{
					session.close();
					return;
				}
				if (deviceId.equals("12345690978679801") && this.manager.getDevice("211005")) {
					IoSession current = this.manager.getSession("211005");
					logger.warn("有数据:"+current.getId()+":");
					if (current.getId() != session.getId()) {
						session.close();
						return;
					}else
					{
						logger.warn("有效数据:"+current.getId()+":");
					}
				}
				// logger.info(deviceId+"before:" +
				// ByteUtils.byte2HexStr(data));
				// 普通上传指令应答
				try {
					byte[] answerMsg = analysis.answerMsg();
					if (answerMsg != null) {
						session.write(answerMsg);

						// SimpleDateFormat df = new
						// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						// new AychWriter("写入数据：" + df.format(new Date()) + "--"
						// + ByteUtils.byte2HexStr(answerMsg),
						// deviceId).start();

					} else {
						// return ;
					}
					byte[] extraAnswerMsg = analysis.extraAnswerMsg();
					if (extraAnswerMsg != null) {

						session.write(extraAnswerMsg);
						logger.info("回复数据：" + deviceId + "--" + ByteUtils.byte2HexStr(extraAnswerMsg));
						//
						// SimpleDateFormat df = new
						// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						// new AychWriter("写入数据：" + df.format(new Date()) + "--"
						// + ByteUtils.byte2HexStr(extraAnswerMsg),
						// deviceId).start();

					}
				} catch (Exception ex) {

					logger.error("解析出错：", ex);
				}

				//logger.info(deviceId + "after:" + ByteUtils.byte2HexStr(data));
				// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss");
				// new AychWriter("收到数据：" + df.format(new Date()) + "--" +
				// ByteUtils.byte2HexStr(data), deviceId).start();

				// data = null;

			} else {
				return;
			}
			// 检查终端的合法性，和数据库中的数据对比
			VehicleInfo vi = publicStaticMap.getVehicles().get(analysis.getDeviceId());
			if (vi == null) {
				Byte commonId = data[2];
				String terminalId = ByteUtils.bytes2Str(data, 24, 6);
				String hex = Integer.toHexString(commonId & 0xFF);  
				logger.info("车辆在数据库中不存在:"+hex+ "-"+terminalId+":"+analysis.getDeviceId());
				// byte[] data = (byte[]) message;
				logger.info("终端源码：" + ByteUtils.byte2HexStr(data));
				//
				// if (this._config.getIsDebug() == 2) {
				// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss");
				// new AychWriter("车辆不存在关闭链接：" + session.getAttribute("ID") +
				// df.format(new Date()) + "--" + session,
				// "closeSession").start();
				// }
				//if(!analysis.getDeviceId().equals("00000000000000000"))
					
				{
				
				   session.closeOnFlush();
				}
				return;
			}

			// logger.info(vi.getDEVICE_ID()+"验证通过:" +
			// ByteUtils.byte2HexStr(data));
			session.setAttribute("pEnum", pEnum);
			session.setAttribute("vehicleObject", vi);

			// 保存信息到kafka
			toJson(analysis, vi, session, data);
			this.manager.addSession(vi.getDEVICE_ID(), deviceId, session);
			vi = null;
		}
	}

	public void toJson(IProtocolAnalysis analysis, VehicleInfo vi, IoSession session, byte[] bytes) {
		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		analysis.toJson(vi, clientIP, bytes);

	}

}
