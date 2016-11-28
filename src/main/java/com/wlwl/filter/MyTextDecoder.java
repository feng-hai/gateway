package com.wlwl.filter;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.sun.deploy.uitoolkit.impl.fx.Utils;
import com.wlwl.utils.ByteUtils;

public class MyTextDecoder extends CumulativeProtocolDecoder {

	private byte[] temp;

	private IFilterControl control;
	
	private Map<Long ,Long> times=new HashMap<Long ,Long>();

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
		
//	    Long now=	new Date().getTime();
//	    Long last=times.get(session.getId());
//		if(!times.containsKey(session.getId()))
//		{
//			times.put(session.getId(), now);
//		}
//		else if(now-last<800)
//		{
//			return true;
//		}
//		times.replace(session.getId(), now);
		
		ByteBuffer buf = in.buf();
		byte[] msg = new byte[buf.limit()];
		buf.get(msg);
		this.control.setMsg(msg);
		
		// 判断是否是完整包
		// 如果不是，判断后续还有没有，不带7e（除7e结尾）的包，拼包解析
		// 如果有7e结尾的包，合并传递到下一个流程
		if (this.control.isHeader()) {
			if (this.control.isEnd()) {
				
				if (this.control.checkRight()) {
					out.write(msg);
				}
				if(temp!=null)
				{
					//temp数据即将丢失，记录日志
				}	
			} else {

				if (temp == null) {
					temp = msg;
				}
				return false;
			}
		} else {
			if (this.control.isEnd()) {

				temp = ByteUtils.byteMerger(temp, msg);

				// 檢查數據的正確性
				if (control.checkRight()) {
					// 把數據變成传递到下一个流程

					out.write(temp);
				}
			} else {
				if (temp == null) {
					temp = msg;
				} else {
					temp = ByteUtils.byteMerger(temp, msg);
				}
				return false;

			}
		}
		msg=null;
		temp = null;
		return true;
	}

}
