package com.wlwl.filter;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	// public boolean decode(IoSession session, IoBuffer in,
	// ProtocolDecoderOutput out) throws Exception {
	//
	//
	// // String ss = new String(msg, "UTF-8");
	//
	// // res.append(ss);
	// // String m = res.toString();
	// // out.write(ss);
	// // out.flush();
	// // System.out.println(m);
	// // if (m.startsWith("<?xml version=\"1.0\"") && m.endsWith("</Name>")) {
	// // System.out.println("--------------------------- enter
	// // --------------");
	// // out.write(m);
	// // out.flush();
	// // res = null;
	// // res = new StringBuffer();
	// // }
	// }
	//
	// public void dispose(IoSession arg0) throws Exception {
	//
	// }
	//
	// public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
	// throws Exception {
	//
	// }

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

		in.mark();// 标记当前位置，以便reset
		ByteBuffer buf = in.buf();
		byte[] msg = new byte[buf.limit()];
		buf.get(msg);
		this.control.setMsg(msg);//为协议解析对象添加数据
		// 判断是否是完整包
		if (this.control.isHeader()) {
			if (msg.length > this.control.getMessageMinLength()) {//是否大于最小包体长度
				// 获取一组数据的长度
				int dataLength = this.control.getLength();
				if (msg.length == dataLength) {
					if (this.control.isEnd()) {//判断是否有结尾
						if (this.control.checkRight()) {
							out.write(this.control);
						} else {
							// 不符合协议要求
							System.out.println("数据检查没有通过，原始数据为："+ByteUtils.byte2HexStr(msg));
							session.close();
						}
					} else {
						// 不符合协议要求
						System.out.println("数据没有闭合，原始数据为："+ByteUtils.byte2HexStr(msg));
						session.close();
					}

				} else if (msg.length > dataLength) {
					byte[] temp = ByteUtils.getSubBytes(msg, 0, dataLength);
					this.control.setMsg(temp);
					if (this.control.isEnd()) {
						if (this.control.checkRight()) {
							out.write(this.control);
						} else {
							// 不符合协议要求
							System.out.println("数据检查没有通过，原始数据为："+ByteUtils.byte2HexStr(msg));
							session.close();
						}
					} else {
						System.out.println("数据没有闭合，原始数据为："+ByteUtils.byte2HexStr(msg));
						session.close();
					}
					in.reset();
					in.position(dataLength + 1);
					return false;

				} else {//小于包体长度，继续获取
					return false;
				}
			} else {//小于最小长度，继续获取
				return false;
			}
		} else {
			System.out.println("数据包头不正确，原始数据为："+ByteUtils.byte2HexStr(msg));
			session.close();
		}
		msg = null;
		return true;	
	}
}
