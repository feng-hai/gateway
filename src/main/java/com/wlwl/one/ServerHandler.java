package com.wlwl.one;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.handler.Handler;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.utils.AychWriter;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.Config;

public class ServerHandler extends IoHandlerAdapter {

	//private IServerHandler handler;
	private ProtocolEnum pEnum;
	private SessionManager manager;
	private Config _config;
	private BlockingQueue<ProtocolModel> _sendQueue;
	private Map<String, VehicleInfo> _vehicles;
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	public ServerHandler(ProtocolEnum pEnum,BlockingQueue<ProtocolModel> sendQueue,
			Map<String, VehicleInfo> vehicles, SessionManager _manager, Config config) {
		this._sendQueue=sendQueue;
		this._vehicles=vehicles;
		this.pEnum=pEnum;
		this.manager = _manager;
		this._config = config;
	}

	@Override
	public  void messageReceived(IoSession session, Object message) throws Exception {
		
		Handler handler=new Handler(this.pEnum,this._sendQueue,this._vehicles,this.manager,this._config,message,session);
		handler.excute();
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (this._config.getIsDebug() == 1) {
			System.out.println("========= server send msg:: " + ByteUtils.byte2HexStr((byte[]) message));
		}
		String id = session.getAttribute("ID").toString();
		List<String> watchs = this._config.getWatchVehiclesList();
		if (this._config.getIsDebug() == 2 && watchs.contains(id)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new AychWriter("发送数据：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr((byte[]) message), id)
					.start();
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		if (this._config.getIsDebug() == 1) {
			System.out.println("======= server session opern:: " + session);
			logger.error("开启连接：" + session.getAttribute("ID") + session);
		}
		if (this._config.getIsDebug() == 2) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new AychWriter("开启链接：" + df.format(new Date()) + "--" + session, "openSession").start();
			session.setAttribute("time", df.format(new Date()));
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if (!session.containsAttribute("old")) {
			this.manager.removeSession(session);
		}
		if (this._config.getIsDebug() == 2) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new AychWriter("关闭链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session,
					"closeSession").start();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		session.close(true);

		if (this._config.getIsDebug() == 2) {
			logger.error("錯誤：" + session.getAttribute("ID") + session);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			cause.printStackTrace(pw);
			logger.error(sw.toString());

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new AychWriter("发生异常关闭链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session,
					"closeSession").start();
		}
	}

	// 当连接空闲时触发此方法.
	public void sessionIdle(IoSession session, IdleStatus arg1) throws Exception {
		if (this._config.getIsDebug() == 2) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new AychWriter("超时关闭链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session,
					"closeSession").start();
		}
		session.close(true);
		// this.manager.removeSession(session);
	}

}
