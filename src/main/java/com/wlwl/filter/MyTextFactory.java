package com.wlwl.filter;


import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.wlwl.enums.ProtocolEnum;


public class MyTextFactory implements ProtocolCodecFactory
{
	private ProtocolEnum pEnum;
	
	public MyTextFactory(ProtocolEnum pEnum)
	{
		this.pEnum=pEnum;
	}

	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception
	{
		return new MyTextDecoder(this.pEnum);
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception
	{
		return new MyTextEncoder();
	}

}
