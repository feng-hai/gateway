package com.wlwl.filter;

import org.apache.mina.common.IoSession;

/**
 * @author FH
 *
 */
/**
 * @author FH
 *
 */
public interface IFilterControl {
	

//
//	/**
//	 * 
//	 * @return
//	 * 头部信息
//	 */
//	Boolean isHeader();
//	
//	/**
//	 * @return
//	 * 判断结尾
//	 */
//	Boolean isEnd(byte[] msg);
//
//	/**
//	 * @return
//	 * 检查包的正确性
//	 * 0、检查是否是完整的包
//	 * 1、开头结尾是否匹配
//	 * 
//	 * 2、验证码是否匹配
//	 */
//	Boolean checkRight();
//	
//	/**
//	 * @return  检查是否超过单例长度
//	 */
//	Boolean	checkLength();
	
	int  getLength();
//	
	/**
	 * 获取协议体的最小长度
	 * @return
	 */
	int getMessageMinLength();
//
	/**
	 * @param msg  設置數據信息
	 */
	void setMsg(byte[] msg,IoSession session);
//	
////	/**
////	 * @return 获取多余的字节数，并保存独立的解析单体  （保存整个信息）
////	 */
//	byte[] getMsg();
	
	
	Boolean isMarker(byte msg);
	

}
