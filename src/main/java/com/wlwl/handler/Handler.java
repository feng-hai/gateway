package com.wlwl.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

public class Handler {
	//private IServerHandler handler;
	private SessionManager manager;
	private Config _config;
	private Object message;
	private IoSession session;
	private ProtocolEnum pEnum;
	private BlockingQueue<ProtocolModel> _sendQueue;
	private Map<String, VehicleInfo> _vehicles;
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	public Handler(ProtocolEnum pEnum,BlockingQueue<ProtocolModel> sendQueue,
			Map<String, VehicleInfo> vehicles, SessionManager _manager, Config config ,Object message,IoSession session)
	{
		//this.handler = _handler;
		this.pEnum=pEnum;
		this._sendQueue=sendQueue;
		this._vehicles=vehicles;
		this.manager = _manager;
		this._config = config;
		this.message=message;
		this.session=session;
	}
	public void excute()
	{
		List<String> watchs = this._config.getWatchVehiclesList();
		
		IProtocolAnalysis analysis=ProtocolFactory.getAnalysis(pEnum);
		byte[] data;
	//	synchronized(this)
		{
		// 解析类赋值
		if (message instanceof byte[]) {
			 data = (byte[]) message;
			if (data == null || data.length < analysis.getMinLength()) {
				if (this._config.getIsDebug() == 2) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					new AychWriter("数据异常：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr(data),
							"ExceptionData").start();
				}
				return;
			}
			analysis.setMsg(data);
			String deviceId = analysis.getDeviceId();

			// 普通上传指令应答
			try {
				byte[] answerMsg = analysis.answerMsg();
				if (answerMsg != null) {
					session.write(answerMsg);
					if (this._config.getIsDebug() == 2 && watchs.contains(deviceId.trim())) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						new AychWriter("写入数据：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr(answerMsg),
								deviceId).start();
					}
				}
			} catch (Exception ex) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				logger.error(sw.toString());
			}

			this.manager.addSession(analysis.getDeviceId(), session);
			if (this._config.getIsDebug() == 2 && watchs.contains(deviceId.trim())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				new AychWriter("收到数据：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr(data), deviceId).start();
			}
			//data = null;

		} else {
			return;
		}
		// 检查终端的合法性，和数据库中的数据对比
		VehicleInfo vi = this._vehicles.get(analysis.getDeviceId());
		if (vi == null) {
			if (this._config.getIsDebug() == 2) {
				logger.info("车辆在数据库中不存在:" + analysis.getDeviceId());
				//byte[] data = (byte[]) message;
				logger.info("终端源码：" + ByteUtils.byte2HexStr(data));
			}
			if (this._config.getIsDebug() == 2) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				new AychWriter("车辆不存在关闭链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session,
						"closeSession").start();
			}
			session.close(true);
			return;
		}
		// 保存信息到kafka
		toJson(analysis,vi,session,data);
		vi = null;
		}
	}
	
	public void toJson(IProtocolAnalysis analysis,VehicleInfo vi, IoSession session,byte[] bytes) {
		ProtocolModel pm = new ProtocolModel();
		// VehicleInfo vi=this._vehicles.

		pm.setDEVICE_ID(vi.getDEVICE_ID());
		pm.setCELLPHONE(vi.getCELLPHONE());
		pm.setProto_unid(analysis.getProtocol());
		pm.setNode_unid(analysis.getNode());
		pm.setUnid(vi.getUNID());
		pm.setRAW_OCTETS(ByteUtils.bytesToHexString(bytes));
		pm.setLength(String.valueOf(pm.getRAW_OCTETS().length() / 2));
		pm.setTIMESTAMP(new Date().getTime());
		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		pm.setIP4(clientIP);
		try {
			this._sendQueue.put(pm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
