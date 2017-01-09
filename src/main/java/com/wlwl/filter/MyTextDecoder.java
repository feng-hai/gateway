package com.wlwl.filter;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.AttributeKey;
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

	private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");

	public Context getContext(IoSession session) throws CloneNotSupportedException {
		Context ctx = (Context) session.getAttribute(CONTEXT);
		if (ctx == null) {
			ctx = new Context();
			ctx.set_control(this.control);
			session.setAttribute(CONTEXT, ctx);
		}
		return ctx;
	}

	private class Context {

		public int minLength;

		public byte[] minBytes;

		public int allLength;

		public byte[] allContent;

		public void clear() {
			minLength = 0;
			minBytes = null;
			allLength = 0;
			allContent = null;
		}

		private IFilterControl _control;

		public IFilterControl get_control() {

			return _control;
		}

		public void set_control(IFilterControl _control) throws CloneNotSupportedException {
			this._control = (IFilterControl) _control.clone();
		}

		// 状态变量
		private IoBuffer _content = null;

		public IoBuffer get_content() {
			return _content;
		}

		public void set_content(IoBuffer content) {

			if (this._content == null) {
				this._content = content;
			} else {
				this._content.put(content);

			}

		}
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		Context content = this.getContext(session);

		IFilterControl controlclone = (IFilterControl) this.control.clone();
		//synchronized (controlclone) {
			in.mark();// 标记当前位置，以便reset
			content.minLength = controlclone.getMessageMinLength();
			if (in.remaining() > content.minLength)// 数据大于最小长度
			{
				content.minBytes = new byte[content.minLength];
				in.get(content.minBytes, 0, content.minLength);
				controlclone.setMsg(content.minBytes);
				if (controlclone.isHeader()) {// 判断包头是否正确
					content.allLength = controlclone.getLength();// 获取包体长度

					in.reset();

					if (content.allLength > in.remaining())// 不够一个完整包的长度，继续取包
					{
						content.clear();
						return false;
					}
					content.allContent = new byte[content.allLength];
					in.get(content.allContent, 0, content.allLength);// 获取一条信息
					//controlclone.setMsg(content.allContent);
					if (controlclone.isEnd(content.allContent)) {// 判断是否有结尾

						if (controlclone.checkRight()) {
							out.write(content.allContent);
							//System.out.println(ByteUtils.byte2HexStr(content.allContent));
							content.clear();
							return true;
						} else {
							// 不符合协议要求
							session.close();
							in.reset();
							ByteBuffer buf = in.buf();
							byte[] msg = new byte[buf.limit()];
							buf.get(msg);
							System.out.println("数据检查没有通过，原始数据为：" + ByteUtils.byte2HexStr(msg));
							// session.close();
						}
					} else {

						 session.close();
						// 不符合协议要求
						System.out.println("原始数据为：" + ByteUtils.byte2HexStr(content.allContent));
						in.reset();
						ByteBuffer buf = in.buf();
						byte[] msg = new byte[buf.limit()];
						buf.get(msg);
						System.out.println("数据没有闭合，原始数据为：" + ByteUtils.byte2HexStr(msg));

					}
				} else {
					 session.close();
					in.reset();
					ByteBuffer buf = in.buf();
					byte[] msg = new byte[buf.limit()];
					buf.get(msg);
					System.out.println("数据包头不正确，原始数据为：" + ByteUtils.byte2HexStr(msg));
				}
			}
			content.clear();
			return false;
		//}

		// ByteBuffer buf = in.buf();
		// byte[] msg = new byte[buf.limit()];
		// buf.get(msg);

		// System.out.println(ByteUtils.byte2HexStr(msg));
		// String hex=ByteUtils.byte2HexStr(msg);
		// String result= hex.replaceAll("7D02", "7E").replaceAll("7D01", "7D");
		// byte[] resultMsg=ByteUtils.hexStr2Bytes(result);
		// this.control.setMsg(minBytes);//为协议解析对象添加数据
		// 判断是否是完整包
		// if (this.control.isHeader()) {
		//
		// if (resultMsg.length > this.control.getMessageMinLength()) {//
		// 是否大于最小包体长度
		// // 获取一组数据的长度
		// int dataLength = this.control.getLength();
		// if (resultMsg.length == dataLength) {
		// if (this.control.isEnd()) {// 判断是否有结尾
		// if (this.control.checkRight(msg)) {
		// out.write(msg);
		// } else {
		// // 不符合协议要求
		// System.out.println("数据检查没有通过，原始数据为：" +
		// ByteUtils.byte2HexStr(resultMsg));
		// session.close();
		// }
		// } else {
		// // 不符合协议要求
		// System.out.println("数据没有闭合，原始数据为：" +
		// ByteUtils.byte2HexStr(resultMsg));
		// session.close();
		// }
		//
		// } else if (resultMsg.length > dataLength) {
		// byte[] temp = ByteUtils.getSubBytes(resultMsg, 0, dataLength);
		// this.control.setMsg(temp);
		// if (this.control.isEnd()) {
		//
		// String temps[] = hex.split("7E");
		// String tempMsg = "7e" + temp[1] + "7e";
		//
		// byte[] tempMsgArray = ByteUtils.hexStr2Bytes(tempMsg);
		//
		// if (this.control.checkRight(tempMsgArray)) {
		//
		// out.write(tempMsgArray);
		//
		// } else {
		// // 不符合协议要求
		// System.out.println("数据检查没有通过，原始数据为：" + ByteUtils.byte2HexStr(msg));
		// session.close();
		// }
		// in.reset();
		// in.position(tempMsgArray.length + 1);
		//
		// } else {
		// System.out.println("数据没有闭合，原始数据为：" + ByteUtils.byte2HexStr(msg));
		// session.close();
		// }
		//
		// return false;
		//
		// } else {// 小于包体长度，继续获取
		// return false;
		// }
		// } else {// 小于最小长度，继续获取
		// return false;
		// }
		// } else {
		// System.out.println("数据包头不正确，原始数据为：" + ByteUtils.byte2HexStr(msg));
		// session.close();
		// }
		// msg = null;
		// return true;

	}
}
