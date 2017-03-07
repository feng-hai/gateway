package com.wlwl.filter;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wlwl.utils.ByteUtils;

public class MyTextDecoder extends CumulativeProtocolDecoder {

	// private byte[] temp;

	private IFilterControl control;

	// private Map<Long, Long> times = new HashMap<Long, Long>();

	public MyTextDecoder(IFilterControl _control) {
		this.control = _control;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		in.mark();
		int position = in.position();
		int remain = in.remaining();
		byte[] allData = new byte[remain];
		in.get(allData);
		if (!this.control.isMarker(allData[0]))// 标识符不对
		{
			session.close(true);
			//System.out.println("消息头不对：" + ByteUtils.byte2HexStr(temp));
			return false;
		}
		in.reset();
		int startIndex = -1;
		int endIndex = -1;
		boolean isFirst = true;
		for (int i = 0; i < allData.length; i++) {
			if (this.control.isMarker(allData[i])) {
				if (isFirst) {
					startIndex = i;
					in.position(i + position);
					isFirst = false;
				} else {
					endIndex = i;
				}
			}
			if (startIndex != -1 && endIndex != -1) {
				int len = endIndex - startIndex + 1;
				if (len == 2) {
					byte[] buf = new byte[1];
					in.get(buf);
					//out.write(buf);
					return true;
				} else {
					byte[] buf = new byte[len];
					in.get(buf);
					out.write(buf);
					return true;
				}
			}
			if (i >= 0xffff) {
				in.position(allData.length-1);
				session.close(true);
				System.out.println("消息体长度不匹配：" + ByteUtils.byte2HexStr(allData));
				return true;
			}
		}
		in.reset();
		return false;

	}
}
