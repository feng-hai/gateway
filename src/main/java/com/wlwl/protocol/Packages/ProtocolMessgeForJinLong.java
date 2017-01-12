package com.wlwl.protocol.Packages;

import java.io.Serializable;

import org.apache.mina.common.IoSession;

import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.utils.BCCUtils;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.CRCUtil;

public class ProtocolMessgeForJinLong implements IProtocolAnalysis, Serializable, Cloneable {

	private String Protocol = "10714621291D4F018DA9F498077AD8BD";// 协议标识，金龙

	/**
	 * 頭部數據
	 */
	private static final long serialVersionUID = 1L;

	private short gpsCommandId;

	private short gpsLength;

	// 属性
	private short attachmentId;

	private short attachmentLength;

	private int sequenceId;

	private String gpsId;

	private byte subDeviceId;

	private byte gpsManufacturers;

	private byte hostCompanies;

	public short getGpsCommandId() {
		return gpsCommandId;
	}

	public void setGpsCommandId(short gpsCommandId) {
		this.gpsCommandId = gpsCommandId;
	}

	public short getGpsLength() {
		return gpsLength;
	}

	public void setGpsLength(short gpsLength) {
		this.gpsLength = gpsLength;
	}

	public short getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(short attachmentId) {
		this.attachmentId = attachmentId;
	}

	public short getAttachmentLength() {
		return attachmentLength;
	}

	public void setAttachmentLength(short attachmentLength) {
		this.attachmentLength = attachmentLength;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(short sequenceId) {
		this.sequenceId = sequenceId;
	}

	public byte getSubDeviceId() {
		return subDeviceId;
	}

	public void setSubDeviceId(byte subDeviceId) {
		this.subDeviceId = subDeviceId;
	}

	public byte getGpsManufacturers() {
		return gpsManufacturers;
	}

	public void setGpsManufacturers(byte gpsManufacturers) {
		this.gpsManufacturers = gpsManufacturers;
	}

	public byte getHostCompanies() {
		return hostCompanies;
	}

	public void setHostCompanies(byte hostCompanies) {
		this.hostCompanies = hostCompanies;
	}

	public String getGpsId() {
		return gpsId;
	}

	public void setGpsId(String gpsId) {
		this.gpsId = gpsId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[Can网络消息命令号/PLC命令号 : ").append(attachmentId).append(",").append("Can网络消息命令号/PLC命令长度 : ")
				.append(attachmentLength).append(",").append("命令ID : ").append(gpsCommandId).append(",")
				.append("终端ID : ").append(gpsId).append(",").append("消息长度 : ").append(gpsLength).append(",")
				.append("Gps供应商代码 : ").append(gpsManufacturers).append(",").append("消息流水号 : ").append(sequenceId)
				.append(",").append("子设备代码 : ").append(subDeviceId).append(",").append("]");
		return builder.toString();
	}

	public byte[] getData(short result) {
		byte[] heart = new byte[15];
		heart[0] = 0x23;
		heart[1] = 0x00;// 消息ID
		heart[2] = 0x00;// 消息体属性
		heart[3] = (byte) 5;// 消息体长度
		heart[4] = 0x00;// 消息体长度1
		heart[5] = (byte) this.getSequenceId();// 消息流水号
		heart[6] = (byte) (this.getSequenceId() >> 8);// 消息流水号1
		heart[8] = (byte) (this.getGpsCommandId());// 原消息ID
		heart[9] = (byte) this.getSequenceId();// 原消息流水号
		heart[10] = (byte) (this.getSequenceId() >> 8);// 原消息流水号1
		heart[11] = (byte) result;// 消息结果 成功
		heart[12] = (byte) (result >> 8);// 消息结果 成功
		heart[13] = BCCUtils.enVerbCode(heart);//
		heart[14] = 0x23;

		return heart;

	}

	/*
	 * 尾部數據
	 */
	private Short crcCode;

	public Short getCrcCode() {
		return crcCode;
	}

	public void setCrcCode(Short crcCode) {
		this.crcCode = crcCode;
	}

	/*
	 * 
	 * 功能判斷
	 */
	private byte[] msg;

	public Boolean checkLength() {
		return true;
	}

	public Boolean checkRight(byte[] bys) {

//		if (bys.length < 23) {
//			return false;
//		}
//
//		byte[] temp = new byte[bys.length - 4];
//
//		for (int i = 1; i < bys.length - 3; i++) {
//			temp[i - 1] = bys[i];
//		}
//
//		int tempCrc = CRCUtil.parseCRCMessageTail(temp);
//
//		if (this.getCrcCode() == tempCrc) {
//			return true;
//		}

		return true;
	}

	public String getDeviceId() {

		return ByteUtils.bytesToAsciiString(this.msg, 10, 10);

	}

	public void setMsg(byte[] bytes, IoSession session) {
		this.msg = bytes;
		this.gpsCommandId = ByteUtils.getShort(this.msg, 1);
		if (this.gpsCommandId == 0x11) {
			this.gpsId = this.getDeviceId();
			session.setAttribute("ID", this.gpsId);
		} else {
			Object temp = session.getAttribute("ID");
			if (temp != null) {
				this.gpsId = temp.toString();
			} else {
				// 终端没有登录
				System.out.println("终端没有登录，就上传信息");
				session.close();
				
				

			}

		}
		this.sequenceId = ByteUtils.getInt(this.msg, 4);
		this.attachmentId = this.msg[1];
		// this.gpsLength = ByteUtils.getShort(this.msg, 3);
		// this.attachmentId = ByteUtils.getShort(this.msg, 5);
		// this.attachmentLength = ByteUtils.getShort(this.msg, 7);
		// this.sequenceId = ByteUtils.getShort(this.msg, 9);
		// this.gpsId = this.getDeviceId();
		// this.subDeviceId = this.msg[17];
		// this.gpsManufacturers=this.msg[18];
		// this.hostCompanies = this.msg[19];
		//
		// //System.out.println(ByteUtils.byte2HexStr(this.msg));
		// this.crcCode = ByteUtils.getShort(this.msg, this.msg.length - 3);
		// TODO Auto-generated method stub

	}

	public Boolean isFull() {

		if (this.msg[0] == (byte) 0x7e && this.msg[this.msg.length - 1] == (byte) 0x7e) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean isHeader() {

		if (this.msg[0] == (byte) 0x7e) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean isEnd(byte[] msg) {
		byte test = msg[this.msg.length - 1];
		if (msg[msg.length - 1] == (byte) 0x7e) {
			return true;
		}
		System.out.println("判断不正确" + ByteUtils.byte2HexStr(msg));
		return false;
	}

	public String getProtocol() {
		return this.Protocol;

	}

	public Boolean answerMsg(IoSession session) {

		// 默认消息结果见：
		// 0x0000 表示命令执行成功
		// 0x0001 表示命令执行失败
		// 0xFFFF 表示无效
		switch (this.msg[1]) {
			case 0x01: {// 心跳
				byte[] data = getData((short) 0x0000);
				session.write(data);
				// 0x00
				break;
			}
			case 0x11: {// 终端注册
				// 0x00
				// 0x0101 注册成功
				// 0x0102 生产商代码不存在
				// 0x0103 车载终端编号不存在
				// 0x0104 生产商、识别码与系统中以有设备信息冲突
				// 0x0105 终端信息、识别码与系统中以有设备信息冲突
	
				byte[] data = getData((short) 0x0101);
				session.write(data);
	
				break;
			}
			case 0x12: {// 终端注销
				// 0x00
	
				// 0x0101 注销成功
				// 0x0102 终端与系统数据库中记录不匹配
				// 0x0103 生产商代码不存在
				// 0x0104 不支持注销_请联系平台管理员在平台注销
				byte[] data = getData((short) 0x0101);
				session.write(data);
				break;
			}
			case (byte) 0x83: {// 数据汇报
				// 0x00
	
				byte[] data = getData((short) 0x0000);
				session.write(data);
				break;
			}
			case (byte) 0xB1: {// 故障汇报
				// 0x00
				byte[] heart = new byte[9];
				heart[0] = 0x23;
				heart[1] = 0x00;// 消息ID
				heart[2] = 0x00;// 消息体属性
				heart[3] = (byte) 5;// 消息体长度
				heart[4] = 0x00;// 消息体长度1
				heart[5] = (byte) this.getSequenceId();// 消息流水号
				heart[6] = (byte) (this.getSequenceId() >> 8);// 消息流水号1
				heart[7] = BCCUtils.enVerbCode(heart);//
				heart[8] = 0x23;
				session.write(heart);
				break;
			}

		}

		return true;

		// if (ByteUtils.getShort(this.msg, 1) == (short) 0x0181) {
		// this.gpsCommandId = (short) 0x0101;
		// } else {
		// this.gpsCommandId = (short) 0x0403;
		// }
		//
		// this.attachmentId = 0;
		// this.attachmentLength = 0;
		// this.gpsLength = 0;
		//
		// byte[] temp = this.getData();
		// short tempCrc = (short) CRCUtil.evalCRC16(temp);
		// byte[] crc = new byte[3];
		// crc[0] = (byte) tempCrc;
		// crc[1] = (byte) (tempCrc >> 8);
		// crc[2] = (byte) (0x7e);
		//
		// byte[] last = ByteUtils.byteMerger(temp, crc);
		//
		// byte[] all = ByteUtils.byteMerger(new byte[] { (byte) 0x7e }, last);
		//
		// session.write(all);
		// temp = null;
		// crc = null;
		// last = null;
		// all = null;
		//
		// return true;

	}

	/*
	 * 获取单例长度
	 * 
	 * @see com.wlwl.protocol.IProtocolAnalysis#getLength()
	 */
	public int getLength(byte[] msg) {

//		int headerLength = 20;// 头部长度
//		int endLength = 3;// 尾部长度
//		int gpsLength = ByteUtils.getShort(this.msg, 3);// gps长度
//		int canLength = ByteUtils.getShort(this.msg, 7);// can 长度
//		int allLength = headerLength + endLength + gpsLength + canLength;
//		if (msg.length > allLength && msg[allLength - 1] != 0x7e) {
//			int tempResult = 0;
//			Boolean isture = false;
//			for (int i = allLength; i < msg.length; i++) {
//				tempResult = i;
//				if (msg[i] == 0x7e) {
//					isture = true;
//					break;
//				}
//			}
//
//			if (isture) {
//				allLength = tempResult + 1;
//			} else {
//				allLength = 10000000;
//			}
//		}

		return 0;
	}

	public int getMinLength() {
		// TODO Auto-generated method stub
		return 8;
	}

	public Boolean isMarker(byte msg) {
		if (msg == (byte) 0x23) {
			return true;
		}
		return false;
	}

	public Boolean answerLogin(IoSession session) {
		// TODO Auto-generated method stub
		//String deviceId = this.getDeviceId();

	return true;
	}

}
