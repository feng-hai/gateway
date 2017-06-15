package com.wlwl.one;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.wlwl.config.PropertyResource;
import com.wlwl.enums.ProtocolEnum;

import com.wlwl.filter.MyTextFactory;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;

import com.wlwl.utils.Config;

public class ServerMainThread extends Thread{
	
	//private IServerHandler handler;
	//private IFilterControl control;
	private SessionManager manager;

	private int port;
	//private BlockingQueue<ProtocolModel> _sendQueue;
	//private Map<String, VehicleInfo> _vehicles;
	private ProtocolEnum pEnum;
	
	public ServerMainThread(int port,ProtocolEnum pEnum,
			 SessionManager _manager)
	{
		//this.handler=_handler;
		//this.control=_control;
		//this._sendQueue=sendQueue;
		//this._vehicles=vehicles;
		this.port=port;
		this.manager=_manager;
	
		this.pEnum=pEnum;
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		IoAcceptor acceptor = new NioSocketAcceptor();
//		acceptor.getSessionConfig().setReadBufferSize(1024000);
//		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextFactory(new My3GProtocol())));
//		acceptor.setHandler(new ServerHandler());
//		try {
//			acceptor.bind(new InetSocketAddress(9015));
//			System.out.println("=========  server bind :: " + 9015);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	public void run() {			
		HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setReadBufferSize(1024);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Integer.parseInt(config.get("mina.readerIdleTime"))); 
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextFactory(this.pEnum)));
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter());//用默认的OrderedThreadPoolExecutor保证同一个session在同一个线程中运行
		acceptor.setHandler(new ServerHandler(this.pEnum,this.manager));
		try {
			acceptor.bind(new InetSocketAddress(port));
			System.out.println("=========  server bind :: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
