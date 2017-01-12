package com.wlwl.one;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.wlwl.filter.IFilterControl;
import com.wlwl.filter.MyTextFactory;
import com.wlwl.protocol.Protocol;

public class ServerMainThread extends Thread{
	
	private IServerHandler handler;
	private IFilterControl control;
	private SessionManager manager;
	
	public ServerMainThread(IServerHandler _handler,IFilterControl _control,SessionManager _manager)
	{
		this.handler=_handler;
		this.control=_control;
		this.manager=_manager;
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
		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setReadBufferSize(1024);
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextFactory(this.control)));
		//acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
		acceptor.setHandler(new ServerHandler(this.handler,this.manager));
		try {
			acceptor.bind(new InetSocketAddress(this.handler.getPort()));
			System.out.println("=========  server bind :: " + this.handler.getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
