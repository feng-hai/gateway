package com.wlwl.filter;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.protocol.ProtocolFactory;
import com.wlwl.utils.ByteUtils;

public class MyTextDecoder extends CumulativeProtocolDecoder {

	// private byte[] temp;

	private ProtocolEnum pEnum;

	// private Map<Long, Long> times = new HashMap<Long, Long>();

	public MyTextDecoder(ProtocolEnum pEnum) {
		this.pEnum=pEnum;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		
		IProtocolAnalysis analysis=ProtocolFactory.getAnalysis(pEnum);
		return analysis.filter(session,in,out);
		
	}
}
