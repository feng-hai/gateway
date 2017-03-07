package com.wlwl.one;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.model.VehicleInfo;
import com.wlwl.utils.AychWriter;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.Config;

public class ServerHandler extends IoHandlerAdapter {

	private IServerHandler handler;
	private SessionManager manager;
	private Config _config;
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	public ServerHandler(IServerHandler _handler, SessionManager _manager, Config config) {
		this.handler = _handler;
		this.manager = _manager;
		this._config = config;
	}

	@Override
	public  void messageReceived(IoSession session, Object message) throws Exception {
		
		List<String> watchs = this._config.getWatchVehiclesList();
		synchronized(this.handler)
		{
		// 解析类赋值
		if (message instanceof byte[]) {
			byte[] data = (byte[]) message;
			if (data == null || data.length < this.handler.getMinLength()) {
				if (this._config.getIsDebug() == 2) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					new AychWriter("数据异常：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr(data),
							"ExceptionData").start();
				}
				return;
			}
			this.handler.setMsg(data);
			String deviceId = this.handler.getDeviceId();

			// 普通上传指令应答
			try {
				byte[] answerMsg = this.handler.answerMsg();
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

			this.manager.addSession(this.handler.getDeviceId(), session);
			if (this._config.getIsDebug() == 2 && watchs.contains(deviceId.trim())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				new AychWriter("收到数据：" + df.format(new Date()) + "--" + ByteUtils.byte2HexStr(data), deviceId).start();
			}
			data = null;

		} else {
			return;
		}
		// 检查终端的合法性，和数据库中的数据对比
		VehicleInfo vi = this.handler.checkLegitimacy();
		if (vi == null) {
			if (this._config.getIsDebug() == 2) {
				logger.info("车辆在数据库中不存在:" + this.handler.getDeviceId());
				byte[] data = (byte[]) message;
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
		this.handler.toJson(vi, session);
		vi = null;
		}

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
