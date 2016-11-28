package com.wlwl.filter;


/**
 * @author FH
 *
 */
public interface IFilterControl {
	


	/**
	 * 
	 * @return
	 * 头部信息
	 */
	Boolean isHeader();
	
	/**
	 * @return
	 * 判断结尾
	 */
	Boolean isEnd();

	/**
	 * @return
	 * 检查包的正确性
	 * 0、检查是否是完整的包
	 * 1、开头结尾是否匹配
	 * 
	 * 2、验证码是否匹配
	 */
	Boolean checkRight();
	
	Boolean	checkLength();

	/**
	 * @param msg  設置數據信息
	 */
	void setMsg(byte[] msg);
}
