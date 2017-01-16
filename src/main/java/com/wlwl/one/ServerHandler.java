package com.wlwl.one;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.model.VehicleInfo;
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
	public void messageReceived(IoSession session, Object message) throws Exception {

		// 解析类赋值
		if (message instanceof byte[]) {
			byte[] data = (byte[]) message;
			// System.out.println(ByteUtils.byte2HexStr(data));
			this.handler.setMsg(data, session);
			String deviceId = this.handler.getDeviceId();
			if (this._config.getWatchVehiclesList().contains(deviceId.trim())) {
				logger.info("终端ID:" + deviceId);
				logger.info("终端源码：" + ByteUtils.byte2HexStr(data));
			}

			data = null;

		} else {
			return;
		}

		// 检查终端的合法性，和数据库中的数据对比
		VehicleInfo vi = this.handler.checkLegitimacy();
		if (vi == null) {
			// 不合法终端端口连接
			if (this._config.getWatchVehiclesList().contains(this.handler.getDeviceId().trim())) {
				logger.info("车辆在数据库中不存在:" + this.handler.getDeviceId());
				byte[] data = (byte[]) message;
				logger.info("终端源码：" + ByteUtils.byte2HexStr(data));

			} else {
				// return;
			}

			// System.out.println("车辆在数据库中不存在：" + this.handler.getDeviceId());
			// session.close(true);
			// return;
		}
		if (this._config.getWatchVehiclesList().contains(this.handler.getDeviceId().trim())) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			logger.info("服务器收到终端数据：" + this.handler.getDeviceId() + df.format(new Date()));

		}
		IoSession iSession = this.manager.getSession(this.handler.getDeviceId());
		if (iSession != null) {
			if (!iSession.equals(session)) {
				// 有些协议断开之前要发送，断开原因

				if (this._config.getWatchVehiclesList().contains(this.handler.getDeviceId().trim())) {

					logger.info("关闭老的Session:" + this.handler.getDeviceId());
				}
				iSession.close(true);
				// 终端合法后，保存连接session
				this.manager.addSession(this.handler.getDeviceId(), session);
			}
		} else {

			this.manager.addSession(this.handler.getDeviceId(), session);
		}

		// System.out.println(this.manager.getCount());
		// 保存信息到kafka
		this.handler.toJson(vi, session);
		// 如果是登录指令自动回复

		// 普通上传指令应答
		if (this.handler.answerMsg(session)) {
			return;
		}
		// this.handler=null;

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("========= server send msg:: " + ByteUtils.byte2HexStr((byte[]) message));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("======= server session opern:: " + session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// 清除已经关闭的连接
		this.manager.removeSession(session);

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		// this.manager.removeSession(session);
	}

}
