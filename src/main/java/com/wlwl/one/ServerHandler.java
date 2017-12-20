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

	//private BlockingQueue<ProtocolModel> _sendQueue;
	//private Map<String, VehicleInfo> _vehicles;
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	public ServerHandler(ProtocolEnum pEnum,
			 SessionManager _manager) {
		//this._sendQueue=sendQueue;
	//	this._vehicles=vehicles;
		this.pEnum=pEnum;
		this.manager = _manager;
	
	}

	@Override
	public  void messageReceived(IoSession session, Object message) throws Exception {
		
		Handler handler=new Handler(this.pEnum,this.manager,message,session);
		handler.excute();
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		
		String id = session.getAttribute("ID").toString();
		logger.error("========= server send msg:: " +id+" "+ ByteUtils.byte2HexStr((byte[]) message));
		
				
	
	
		
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		
			logger.info("开启连接：" + session.getAttribute("ID") + session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if (!session.containsAttribute("old")) {
			//this.manager.removeSession(session);
		}
			logger.info("关闭链接：" + session.getAttribute("ID") + "--" + session);
		
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
	

		
			logger.error("錯誤：" + session.getAttribute("ID") + session);

			logger.error("exceptionCaught",cause);

//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			new AychWriter("发生异常关闭链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session,
//					"closeSession").start();
	
		
		//session.close(true);
	}

	// 当连接空闲时触发此方法.
	public void sessionIdle(IoSession session, IdleStatus arg1) throws Exception {
		
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("超时关闭链接：" + session.getAttribute("ID") + df.format(new Date()) + "--" + session)
					;
	
		//session.close(true);
		// this.manager.removeSession(session);
	}

}
