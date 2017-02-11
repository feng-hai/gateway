package com.wlwl.one;

import org.apache.mina.common.IoSession;

import com.wlwl.model.VehicleInfo;

/**
 * @author FH
 *
 */
/**
 * @author FH
 *
 */
/**
 * @author FH
 *
 */
public interface IServerHandler {
	

	/**
	 * @param session  登录回码
	 * @return
	 */
	Boolean answerLogin();
	

	/**
	 * 应答信息
	 * @param session
	 * @return
	 */
	byte[] answerMsg();
	

	/**
	 * @return
	 * 检查合法性
	 */
	VehicleInfo checkLegitimacy();

	/**
	 * 获取端口
	 * @return
	 */
	int getPort();
	
	/**
	 * @param bytes 设置原始数据
	 */
	void setMsg(byte[] bytes);
	
	/**
	 * @return 实体的最小长度
	 */
	int getMinLength();
	/**
	 * @return
	 * 获取终端Id
	 */
	String getDeviceId();
	
	/**
	 *把终端换成json数据
	 */
	void toJson(VehicleInfo vi,IoSession session);
	 
	


}
