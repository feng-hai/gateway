package com.wlwl.protocol;

import org.apache.mina.common.IoSession;

/**
 * @author FH
 *
 */
public interface IProtocolAnalysis {

	/**
	 * @return 
	 * 检查总长度是否正确
	 */
	Boolean checkLength();

	/**
	 * @return 
	 * 校验码是否正确
	 */
	Boolean checkRight();

	/**
	 * @return 
	 * 获取终端Id
	 */
	String getDeviceId();

	/**
	 * @param bytes
	 *            设置原始数据信息
	 */
	void setMsg(byte[] bytes);

	String getProtocol();

	Boolean answerMsg(IoSession session);

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
	Boolean isEnd();
}
