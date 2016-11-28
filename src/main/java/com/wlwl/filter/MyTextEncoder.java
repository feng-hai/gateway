package com.wlwl.filter;


import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MyTextEncoder implements ProtocolEncoder {

	public void dispose(IoSession arg0) throws Exception {
	}

	public void encode(IoSession arg0, Object message, ProtocolEncoderOutput out) throws Exception {
		if (message instanceof byte[]) {
			byte[] data = (byte[]) message;
			IoBuffer buffer = IoBuffer.wrap(data);
			out.write(buffer);

		}

	}

}
