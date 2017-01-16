package com.wlwl.protocol.Packages;

import java.io.Serializable;

import org.apache.mina.common.IoSession;

import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.CRCUtil;

public class ProtocolMessgeFor3G implements IProtocolAnalysis, Serializable, Cloneable {

	private String Protocol = "CD039E17A8E84137AF6DE1CDC172C274";// 协议标识，3协议的网关
	  private String Node="3CE0CF193D67408E80346E0C20263DC6";//节点标识
	/**
	 * 頭部數據
	 */
	private static final long serialVersionUID = 1L;

	private short gpsCommandId;

	private short gpsLength;

	private short attachmentId;

	private short attachmentLength;

	private short sequenceId;

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

	public short getSequenceId() {
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

	public byte[] getData() {
		byte[] data = new byte[19];

		data[0] = (byte) gpsCommandId;
		data[1] = (byte) (gpsCommandId >> 8);
		data[2] = (byte) gpsLength;
		data[3] = (byte) (gpsLength >> 8);
		data[4] = (byte) attachmentId;
		data[5] = (byte) (attachmentId >> 8);
		data[6] = (byte) attachmentLength;
		data[7] = (byte) (attachmentLength >> 8);
		data[8] = (byte) sequenceId;
		data[9] = (byte) (sequenceId >> 8);

		if (gpsId.length() >= 6) {
			for (int i = 0; i < 6; i++) {
				data[i + 10] = (byte) gpsId.charAt(i);
			}
		} else {
			for (int i = 0; i < gpsId.length(); i++) {
				data[i + 10] = (byte) gpsId.charAt(i);
			}
		}

		data[16] = (byte) subDeviceId;
		data[17] = (byte) gpsManufacturers;
		data[18] = (byte) hostCompanies;

		return data;

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
		if (this.msg.length > 23) {
			// 固定长度 头部20 +尾部3个字节
			short fixLength = 23;
			// gps信息长度 3-4字节
			short gpsLng = ByteUtils.getShort(this.msg, 4);
			// Can网络消息长度/PLC消息长度 5-6字节
			short canLng = ByteUtils.getShort(this.msg, 6);

			int allLng = fixLength + gpsLng + canLng;

			if (allLng >= this.msg.length) {
				return true;
			} else {
				return false;
			}
		} else {

			return true;
		}
	}

	public Boolean checkRight(byte[] bys) {

		if (bys.length < 23) {
			return false;
		}
		byte[] temp = new byte[bys.length - 4];

		for (int i = 1; i < bys.length - 3; i++) {
			temp[i-1] = bys[i];
		}
		int tempCrc = CRCUtil.parseCRCMessageTail(temp);

		if (this.getCrcCode() == tempCrc) {
			return true;
		}
		return false;
	}

	public String getDeviceId() {
		String temp=ByteUtils.byte2HexStr(this.msg);
		temp=temp.replaceAll("7D02", "7E");
	 	temp=temp.replaceAll("7D01", "7D");
		return ByteUtils.bytesToAsciiString(ByteUtils.hexStr2Bytes(temp),  11, 6);

	}

	public void setMsg(byte[] bytes,IoSession session) {
		this.msg = bytes;
		String temp=ByteUtils.byte2HexStr(this.msg);
		temp=temp.replaceAll("7D02", "7E");
	 	temp=temp.replaceAll("7D01", "7D");
	 	this.msg=ByteUtils.hexStr2Bytes(temp);
		this.gpsCommandId = ByteUtils.getShort(this.msg, 1);//获取消息id
		this.gpsLength = ByteUtils.getShort(this.msg, 3);
		this.attachmentId = ByteUtils.getShort(this.msg, 5);
		this.attachmentLength = ByteUtils.getShort(this.msg, 7);
		this.sequenceId = ByteUtils.getShort(this.msg, 9);
		
		this.subDeviceId = this.msg[17];
		this.gpsManufacturers=this.msg[18];
		this.hostCompanies = this.msg[19];
		
		//System.out.println(ByteUtils.byte2HexStr(this.msg));
		this.crcCode = ByteUtils.getShort(this.msg, this.msg.length - 3);
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
		byte test=msg[this.msg.length - 1] ;
		if (msg[msg.length - 1] == (byte) 0x7e) {
			return true;
		}
		System.out.println("判断不正确"+ByteUtils.byte2HexStr(msg));
		return false;
	}

	public String getProtocol() {
		return this.Protocol;

	}

	public Boolean answerMsg(IoSession session) {

		if (ByteUtils.getShort(this.msg, 1) == (short) 0x0181 || ByteUtils.getShort(this.msg, 1) == (short) 0x02E7) {

			if (ByteUtils.getShort(this.msg, 1) == (short) 0x0181) {
				this.gpsCommandId = (short) 0x0101;
			} else {
				this.gpsCommandId = (short) 0x0267;
			}

			this.attachmentId = 0;
			this.attachmentLength = 0;
			this.gpsLength = 0;

			byte[] temp = this.getData();
			short tempCrc = (short) CRCUtil.evalCRC16(temp);
			byte[] crc = new byte[3];
			crc[0] = (byte) tempCrc;
			crc[1] = (byte) (tempCrc >> 8);
			crc[2] = (byte) (0x7e);

			byte[] last = ByteUtils.byteMerger(temp, crc);

			byte[] all = ByteUtils.byteMerger(new byte[] { (byte) 0x7e }, last);

			session.write(all);
			temp=null;
			crc=null;
			last=null;
			all=null;

			return true;
		}
		return false;
	}

	/* 获取单例长度
	 * @see com.wlwl.protocol.IProtocolAnalysis#getLength()
	 */
	public int getLength(byte[] msg) {
		
		String temp=ByteUtils.byte2HexStr(this.msg);
	

		int gpsLength=ByteUtils.getShort(this.msg, 3);//gps长度
		int canLength=ByteUtils.getShort(this.msg, 7);//can 长度	
		int allLength=gpsLength+canLength;
		return allLength;
	}

	public int getMinLength() {
		// TODO Auto-generated method stub
		return 23;
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
		return this.Node;
	}



}
