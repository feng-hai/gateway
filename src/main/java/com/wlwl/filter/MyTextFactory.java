package com.wlwl.filter;


import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


public class MyTextFactory implements ProtocolCodecFactory
{
	private IFilterControl control;
	public MyTextFactory(IFilterControl _control)
	{
		this.control=_control;
	}

	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception
	{
		return new MyTextDecoder(this.control);
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception
	{
		return new MyTextEncoder();
	}

}
