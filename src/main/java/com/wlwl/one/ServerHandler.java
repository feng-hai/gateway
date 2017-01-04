package com.wlwl.one;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import com.wlwl.model.VehicleInfo;

public class ServerHandler extends IoHandlerAdapter {

	private IServerHandler handler;
	private SessionManager manager;
	

	public ServerHandler(IServerHandler _handler, SessionManager _manager) {
		this.handler = _handler;
		this.manager = _manager;
	}
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		
		// 解析类赋值
		if (message instanceof byte[]) {
			byte[] data = (byte[]) message;
			this.handler.setMsg(data);
			data=null;
			
		}
		//检查终端的合法性，和数据库中的数据对比
		VehicleInfo vi=this.handler.checkLegitimacy();
		if (vi==null) {
			// 不合法终端端口连接
			session.close(true);
			return;
		}
		// 终端合法后，保存连接session
		this.manager.addSession(this.handler.getDeviceId(), session);
		//System.out.println(this.manager.getCount());
		// 保存信息到kafka
		this.handler.toJson(vi,session);
        //如果是登录指令自动回复
		if (this.handler.answerLogin(session)) {
			return;
		}
        //普通上传指令应答
		if (this.handler.answerMsg(session)) {
			return;
		}
		//this.handler=null;

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		//System.out.println("=========  server send msg:: " + message);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		//System.out.println("======= server session opern:: " + session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// 清除已经关闭的连接
		this.manager.removeSession(session);

	}

}
