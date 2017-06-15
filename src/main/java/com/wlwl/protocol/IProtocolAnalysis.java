package com.wlwl.protocol;

import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;

/**
 * @author FH
 *
 */
public interface IProtocolAnalysis {

//	/**
//	 * @return 
//	 *获取数据的总长度
//	 */
//	int getLength(byte[] msg);
//	
	/**
	 * 获取协议最小长度
	 * @return
	 */
	int getMinLength();
//
//	/**
//	 * @return 
//	 * 校验码是否正确
//	 */
//	Boolean checkRight(byte[] bys);

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
	
	String getNode();

	byte[] answerMsg();

	/**
	 * @return 设置额外应答
	 */
	byte[] extraAnswerMsg();
	
	/**
	 * 终端登录应答
	 * @param session
	 * @return
	 */
//	public byte[] answerLogin();

	/**
	 * @return
	 * 
	 * ，是否是閉合的原始嗎流
	 */
	//Boolean isFull();

	/**
	 * @return
	 * 判断开头
	 */
	//Boolean isHeader();

	/**
	 * @return
	 * 判断结尾
	 */
	//Boolean isEnd(byte[] msg);
	
	//
	/**判断是否是标识字符
	 * @param msg
	 * @return
	 */
	//Boolean isMarker(byte msg);
	
	Boolean filter(IoSession session,IoBuffer in,ProtocolDecoderOutput out);
	
	/**
	 * 存入队列
	 * @param vi
	 * @param ip
	 * @param bytes
	 */
	void toJson(VehicleInfo vi, String ip,byte[] bytes);
	
	/**
	 * 发送之前处理发送数据，如：用国标的协议包装3G协议
	 * @param sendBytes
	 * @return
	 */
	byte[] sendBefore(byte[] sendBytes,VehicleInfo vehicle);
	
}
