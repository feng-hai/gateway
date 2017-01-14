package com.wlwl.protocol.Packages;

import java.io.Serializable;

import org.apache.mina.common.IoSession;

import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.utils.BCDUtils;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.CRCUtil;

public class ProtocolMessageFor808 implements IProtocolAnalysis, Serializable, Cloneable {

	private String Protocol = "AF27DA9036174426A2E2F7C19A9A959C";// 协议标识，3协议的网关
	 private String Node="3CE0CF193D67408E80346E0C20263DC6";//节点标识
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] msg;

	// 消息头
	// 消息 ID
	private short commandId;

	// 消息体属性
	// 是否分包，若第13位为0，则消息头中无消息包封装项字段。
	private byte isSubpackage;

	// 消息体长度
	private short msgLength;
	
	private short  serialNumber;//终端流水号



	// 验证码
	private short crcCode;

	public byte[] getMsg() {
		return msg;
	}

	public short getCommandId() {
		
		return commandId;
	}

	public byte getIsSubpackage() {
		return isSubpackage;
	}

	public short getMsgLength() {
		return msgLength;
	}

	public short getCrcCode() {
		
		return crcCode;
	}

	public Boolean checkLength() {

		return true;
	}

	public Boolean checkRight(byte[] bys) {

		return true;
	}

	/* (non-Javadoc)
	 * @see com.wlwl.protocol.IProtocolAnalysis#getDeviceId()
	 * // 终端唯一标识，808 是用手机号码作为终端的唯一标识的
	 */
	public String getDeviceId() {
		
		  //第五位是手机号码的开始位
		  byte[] telephone=ByteUtils.getSubBytes(this.msg, 5, 6);
		  
		  String teleStr=BCDUtils.bcd2Str(telephone);
		
		  return teleStr;
	}

	public void setMsg(byte[] bytes,IoSession session) {

		this.msg = bytes;
		
		this.commandId=ByteUtils.getShort(this.msg, 1);
		this.serialNumber=ByteUtils.getShort(this.msg, 11);
	}

	public String getProtocol() {
		return this.Protocol;
	}

	public Boolean answerMsg(IoSession session) {
		byte[] answerBytes = new byte[20];
		answerBytes[0] = 0x7e;// 标识符
		answerBytes[answerBytes.length - 1] = 0x7e;// 标识符

		switch (this.commandId) {

		case (short) 0x0100:// 注册

		case (short) 0x0002:// 心跳
		{
			// 命令ID
			answerBytes[1] = (byte) 0x8100;
			answerBytes[2] = (byte) 0x8100 >> 8;
			break;
		}
		default:
			return false;
		}

		// 设置信息体长度
		answerBytes[4] = (byte) 5;
		// 设置手机号码
		for (int i = 5; i < 11; i++) {
			answerBytes[i] = this.msg[i];
		}
		// 设置消息体
		// 应答流水号-》终端上传流水号

		answerBytes[13] = (byte) this.serialNumber;
		answerBytes[14] = (byte) (this.serialNumber >> 8);

		// 应答id -》终端上传命令id

		answerBytes[15] = (byte) this.commandId;
		answerBytes[16] = (byte) (this.commandId >> 8);

		answerBytes[17] = (byte) 1;
		// 设置验证码
		answerBytes[18] = CRCUtil.crc808(answerBytes);

		session.write(answerBytes);

		return true;
	}

	public Boolean isFull() {

		return null;
	}

	public Boolean isHeader() {

		if (this.msg[0] == (byte) 0x7e) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean isEnd(byte[]msg) {
		if (msg[msg.length - 1] == (byte) 0x7e) {
			return true;
		}
		return false;
	}

	public int getLength(byte[] msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override  
    public Object clone()  {  
        try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;  
    }  
	
	public	Boolean isMarker(byte msg)
	{
		if (msg == (byte) 0x7e) {
			return true;
		}
		return false;
	}

	public Boolean answerLogin(IoSession session) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
