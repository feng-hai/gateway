package com.wlwl.protocol;

import org.apache.mina.common.IoSession;

/**
 * @author FH
 *
 */
public interface IProtocolAnalysis {

	/**
	 * @return 
	 *获取数据的总长度
	 */
	int getLength(byte[] msg);
	
	/**
	 * 获取协议最小长度
	 * @return
	 */
	int getMinLength();

	/**
	 * @return 
	 * 校验码是否正确
	 */
	Boolean checkRight(byte[] bys);

	/**
	 * @return 
	 * 获取终端Id
	 */
	String getDeviceId();

	/**
	 * @param bytes
	 *            设置原始数据信息
	 */
	void setMsg(byte[] bytes,IoSession session);

	String getProtocol();
	
	String getNode();

	Boolean answerMsg(IoSession session);
	
	/**
	 * 终端登录应答
	 * @param session
	 * @return
	 */
	public Boolean answerLogin(IoSession session);

	/**
	 * @return
	 * 
	 * ，是否是閉合的原始嗎流
	 */
	Boolean isFull();

	/**
	 * @return
	 * 判断开头
	 */
	Boolean isHeader();

	/**
	 * @return
	 * 判断结尾
	 */
	Boolean isEnd(byte[] msg);
	
	//
	/**判断是否是标识字符
	 * @param msg
	 * @return
	 */
	Boolean isMarker(byte msg);
}
