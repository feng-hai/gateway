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
		if (remain <= this.control.getMessageMinLength())// 小于最小长度
		{
			return false;
		}
		byte[] temp = new byte[remain];
		in.get(temp);
		//this.control.setMsg(temp, session);

		if (!this.control.isMarker(temp[0]))// 标识符不对
		{
			session.close(true);
			System.out.println("消息头不对：" + ByteUtils.byte2HexStr(temp));
			return true;
		}
		
	
		// 获取消息体长度
		//int allMinLength = this.control.getLength();
//
//		if (temp.length < allMinLength + this.control.getMessageMinLength()) {
//			// 数据长度不够
//			return false;
//		}
//
		//int allMaxlength = this.control.getMessageMinLength() + 2 * allMinLength;

		// System.out.println( ByteUtils.byte2HexStr(temp));
		in.reset();
		int startIndex = -1;
		int endIndex = -1;
		boolean isFirst = true;

		for (int i = 0; i < temp.length; i++) {
			if (this.control.isMarker(temp[i])) {
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
					in.get(buf, 0, 1);
					out.write(buf);
					return true;
				} else {
					byte[] buf = new byte[len];
					in.get(buf, 0, len);
					out.write(buf);
					return true;
				}
			}
			if (i >= 0xffff) {
				in.position(temp.length);
				session.close(true);
				System.out.println("消息体长度不匹配：" + ByteUtils.byte2HexStr(temp));
				return true;
			}
		}
		in.reset();
		return false;

	}
}
