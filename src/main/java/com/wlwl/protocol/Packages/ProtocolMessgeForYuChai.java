package com.wlwl.protocol.Packages;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.protocol.IProtocolAnalysis;

import com.wlwl.utils.BCDUtils;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.CRCUtil;
import com.wlwl.utils.publicStaticMap;

public class ProtocolMessgeForYuChai implements IProtocolAnalysis, Serializable, Cloneable {

	private String Protocol = "B443F9C7E040405488364860E9AFF945";// 协议标识，玉柴协议
	private String Node = "3CE0CF193D67408E80346E0C20263DC6";// 节点标识

	private Logger logger = LoggerFactory.getLogger(ProtocolMessgeForYuChai.class);
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
	private byte crcCode;

	public byte getCrcCode() {
		return crcCode;
	}

	public void setCrcCode(byte crcCode) {
		this.crcCode = crcCode;
	}

	/*
	 * 
	 * 功能判斷
	 */
	private byte[] msg;

	public Boolean checkLength() {
		// if (this.msg.length > 23) {
		// // 固定长度 头部20 +尾部3个字节
		// short fixLength = 23;
		// // gps信息长度 3-4字节
		// short gpsLng = ByteUtils.getShort(this.msg, 4);
		// // Can网络消息长度/PLC消息长度 5-6字节
		// short canLng = ByteUtils.getShort(this.msg, 6);
		//
		// int allLng = fixLength + gpsLng + canLng;
		//
		// if (allLng >= this.msg.length) {
		// return true;
		// } else {
		// return false;
		// }
		// } else {
		//
		// return true;
		// }
		return true;
	}

	public Boolean checkRight(byte[] bys) {

		if (bys.length < 9) {
			return false;
		}
		byte[] temp = new byte[bys.length - 3];

		for (int i = 1; i < bys.length - 2; i++) {
			temp[i - 1] = bys[i];
		}
		byte tempCrc = CRCUtil.crc808(temp);

		if ((this.getCrcCode() & 0xFF) == (tempCrc & 0xFF)) {
			return true;
		}
		return false;
	}

	public String getDeviceId() {
		// String temp = ByteUtils.byte2HexStr(this.msg);
		// temp = temp.replaceAll("7D02", "7E");
		// temp = temp.replaceAll("7D01", "7D");
		// this.gpsId=ByteUtils.bytesToAsciiString(this.msg, 11, 6);
		return this.gpsId;

	}

	public void setMsg(byte[] bytes, IoSession session) {

		this.msg = descape(bytes);
		// String temp = ByteUtils.byte2HexStr(this.msg);
		// temp = temp.replaceAll("7D02", "7E");
		// temp = temp.replaceAll("7D01", "7D");
		// this.msg = ByteUtils.hexStr2Bytes(temp);

		this.gpsCommandId = this.msg[1];// ByteUtils.getShort(this.msg, 1);//
										// 获取消息id
		this.gpsLength = ByteUtils.getShortForLarge(this.msg, 5);

		if ((this.gpsCommandId & 0xFF) == 0x80) {// 登录
			logger.debug("登录开始" + ByteUtils.bytesToHexString(this.msg));
			byte[] tempDevice = ByteUtils.getSubBytes(this.msg, 8, 4);
			String deviceId = ByteUtils.byte2HexStr(tempDevice);
			this.setGpsId(deviceId);
			session.setAttribute("ID", deviceId);
			logger.debug("登录成功：终端编号" + deviceId + ":" + ByteUtils.bytesToHexString(this.msg));

		} else if ((this.gpsCommandId & 0xff) == 0x81) {// 登出
			logger.debug("登出开始" + ByteUtils.bytesToHexString(this.msg));
			byte[] tempDevice = ByteUtils.getSubBytes(this.msg, 8, 4);
			String deviceId = ByteUtils.byte2HexStr(tempDevice);
			this.setGpsId(deviceId);
			session.setAttribute("ID", deviceId);
			logger.debug("登出成功：终端编号" + deviceId + ":" + ByteUtils.bytesToHexString(this.msg));
		} else {
			Object deviceO = session.getAttribute("ID");
			if (deviceO != null) {
				logger.debug("登出成功：终端编号" + deviceO.toString() + ":" + ByteUtils.bytesToHexString(this.msg));
				this.setGpsId(deviceO.toString());
			}
		}

		// this.attachmentId = ByteUtils.getShort(this.msg, 5);
		// this.attachmentLength = ByteUtils.getShort(this.msg, 7);
		// this.sequenceId = ByteUtils.getShort(this.msg, 9);
		// this.gpsId = ByteUtils.bytesToAsciiString(this.msg, 11, 6);
		// this.subDeviceId = this.msg[17];
		// this.gpsManufacturers = this.msg[18];
		// this.hostCompanies = this.msg[19];

		// System.out.println(ByteUtils.byte2HexStr(this.msg));
		this.crcCode = this.msg[this.msg.length - 2]; // ByteUtils.getShort(this.msg,
														// this.msg.length - 2);
		// TODO Auto-generated method stub

	}

	public Boolean isFull() {

		if (this.msg[0] == (byte) 0x7b && this.msg[this.msg.length - 1] == (byte) 0x7b) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean isHeader() {

		if (this.msg[0] == (byte) 0x7b) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean isEnd(byte[] msg) {
		byte test = msg[this.msg.length - 1];
		if (msg[msg.length - 1] == (byte) 0x7d) {
			return true;
		}
		System.out.println("判断不正确" + ByteUtils.byte2HexStr(msg));
		return false;
	}

	public String getProtocol() {
		return this.Protocol;
	}

	public byte[] answerMsg(Boolean right) {

		byte[] answerBytes;
		switch (this.gpsCommandId & 0xFF) {
		case (short) 0x80:// 注册或登录
		case (short) 0x81:// 注销或登出
		case (short) 0x70://终端上报警情
		case (short) 0x50://终端上传CAN数据采集
		case (short) 0x52://1终端上传精细数据包
		{
			answerBytes = new byte[13];
			answerBytes[0] = 0x7b;// 标识符
			answerBytes[answerBytes.length - 1] = 0x7d;// 标识符
			// 命令ID
			if ((this.msg[1] & 0xff) == 0x80) {
				answerBytes[1] = 0;
			} else if ((this.msg[1] & 0xff) == 0x81) {
				answerBytes[1] = 1;
			} else if ((this.msg[1] & 0xff) == 0x70) {
				answerBytes[1] = (byte) 0xF0;
			} else if ((this.msg[1] & 0xff) == 0x50) {
				answerBytes[1] = (byte) 0xD0;
			} else if ((this.msg[1] & 0xff) == 0x52) {
				answerBytes[1] = (byte) 0xD2;
			}
			// 信息流水号
			for (int i = 2; i < 5; i++) {
				answerBytes[i] = this.msg[i];
			}
			// 消息体长度
			if ((this.msg[1] & 0xff) == 0x52) {
				answerBytes[5] = 4;
			} else {
				answerBytes[5] = 1;
			}
			answerBytes[6] = 0;

			// 消息内容 0是正确，1是错误
			if (right)// 检查数据合法性（在数据库中是否存在）
			{

				if (checkRight(this.msg))// 检查校验码
				{
					answerBytes[7] = 0;
				} else {
					answerBytes[7] = 1;
				}
			} else {
				answerBytes[7] = 1;
			}
			for (int i = 8; i < 11; i++) {
				answerBytes[i] = this.msg[i];
			}
			// 设置验证码
			answerBytes[11] = CRCUtil.crc808(answerBytes);
			break;
		}
	
		default:
			return null;
		}
		// session.write(escape(answerBytes));

		return escape(answerBytes);

	}

	/*
	 * 获取单例长度
	 * 
	 * @see com.wlwl.protocol.IProtocolAnalysis#getLength()
	 */
	public int getLength(byte[] msg) {

		String temp = ByteUtils.byte2HexStr(this.msg);
		int gpsLength = ByteUtils.getShort(this.msg, 3);// gps长度
		int canLength = ByteUtils.getShort(this.msg, 7);// can 长度
		int allLength = gpsLength + canLength;
		return allLength;
	}

	public int getMinLength() {
		// TODO Auto-generated method stub
		return 9;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Boolean isMarker(byte msg) {
		if (msg == (byte) 0x7b) {
			return true;
		}
		return false;
	}

	public Boolean isMarkerEnd(byte msg) {
		if (msg == (byte) 0x7d) {
			return true;
		}
		return false;
	}

	public byte[] answerLogin() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNode() {
		// TODO Auto-generated method stub
		return this.Node;
	}

	private static final byte OX7C = 0x7c;

	private static final byte[] OX7C_ESCAPE = { 0x7c, 0x0c };

	public static final byte OX7B = 0x7b;

	private static final byte[] OX7B_ESCAPE = { 0x7c, 0x0b };

	/**
	 * 还原转义
	 */
	protected byte[] descape(byte[] octets) {
		if (octets != null && octets.length > 2) {
			ByteBuffer buffer = ByteBuffer.allocate(octets.length);
			buffer.put(octets[0]);// head
			int i = 1;
			for (; i < octets.length - 2; i++) {
				if (octets[i] == OX7C) {
					if (octets[i + 1] == OX7B_ESCAPE[1]) {
						buffer.put(OX7B);
						i++;
					} else if (octets[i + 1] == OX7C_ESCAPE[1]) {
						buffer.put(OX7C);
						i++;
					} else {
						buffer.put(octets[i]);
					}
				} else {
					buffer.put(octets[i]);
				}
			}
			if (i == octets.length - 2)
				buffer.put(octets[octets.length - 2]);
			buffer.put(octets[octets.length - 1]);
			buffer.flip();
			byte[] octetsDescaped = new byte[buffer.remaining()];
			buffer.get(octetsDescaped);
			return octetsDescaped;
		}
		return null;
	}

	// private byte[] messageType = new byte[2];

	/**
	 * 转义
	 */
	protected byte[] escape(byte[] octets) {
		if (octets != null && octets.length > 2) {
			byte[] octetsDescaped = octets;
			ByteBuffer buffer = ByteBuffer.allocate(octetsDescaped.length * 2);
			buffer.put(octetsDescaped[0]);// head
			for (int i = 1; i < octetsDescaped.length - 1; i++) {
				if (octetsDescaped[i] == OX7B)
					buffer.put(OX7B_ESCAPE);
				else if (octetsDescaped[i] == OX7C)
					buffer.put(OX7C_ESCAPE);
				else
					buffer.put(octetsDescaped[i]);
			}
			buffer.put(octetsDescaped[octetsDescaped.length - 1]);// tail
			buffer.flip();
			byte[] octetsEscaped = new byte[buffer.remaining()];
			buffer.get(octetsEscaped);
			return octetsEscaped;
		}
		return new byte[0];
	}

	public Boolean filter(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		// TODO Auto-generated method stub
		in.mark();
		int position = in.position();
		int remain = in.remaining();
		byte[] allData = new byte[remain];
		in.get(allData);
		if (!isMarker(allData[0]))// 标识符不对
		{
			session.close(true);
			// System.out.println("消息头不对：" + ByteUtils.byte2HexStr(temp));
			return false;
		}
		in.reset();
		int startIndex = -1;
		int endIndex = -1;
		// boolean isFirst = true;
		for (int i = 0; i < allData.length; i++) {
			if (isMarker(allData[i])) {
				// if (isFirst) {
				startIndex = i;
				in.position(i + position);
				// isFirst = false;
				// } else {
				// endIndex = i;
				// }
			} else if (isMarkerEnd(allData[i])) {
				endIndex = i;
			}

			if (startIndex != -1 && endIndex != -1) {
				int len = endIndex - startIndex + 1;
				if (len == 2) {
					byte[] buf = new byte[1];
					in.get(buf);
					// out.write(buf);
					return true;
				} else {
					byte[] buf = new byte[len];
					in.get(buf);
					out.write(buf);
					return true;
				}
			}
			if (i >= 0xffff) {
				in.position(allData.length - 1);
				session.close(true);
				System.out.println("消息体长度不匹配：" + ByteUtils.byte2HexStr(allData));
				return true;
			}
		}
		in.reset();
		return false;

	}

	public void toJson(VehicleInfo vi, String ip, byte[] bytes) {
		// TODO Auto-generated method stub
		ProtocolModel pm = new ProtocolModel();
		pm.setDEVICE_ID(vi.getDEVICE_ID());
		pm.setCELLPHONE(vi.getCELLPHONE());
		pm.setProto_unid(getProtocol());
		pm.setNode_unid(getNode());
		pm.setUnid(vi.getUNID());
		pm.setRAW_OCTETS(ByteUtils.bytesToHexString(bytes));
		pm.setLength(String.valueOf(pm.getRAW_OCTETS().length() / 2));
		pm.setTIMESTAMP(Long.toString(new Date().getTime()));
		pm.setFIBER_UNID(vi.getFIBER_UNID());
		pm.setIP4(ip);
		try {
			publicStaticMap.getSendQueue().put(pm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public byte[] sendBefore(byte[] sendBytes, VehicleInfo vehicle) {
		// TODO Auto-generated method stub
		return sendBytes;
	}

	public byte[] extraAnswerMsg() {
		// TODO Auto-generated method stub
		return null;
	}

}
