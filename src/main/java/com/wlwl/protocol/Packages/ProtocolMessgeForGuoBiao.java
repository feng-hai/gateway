package com.wlwl.protocol.Packages;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.utils.AychWriter;
import com.wlwl.utils.BCCUtils;
import com.wlwl.utils.BCDUtils;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.CRCUtil;
import com.wlwl.utils.MessageTools;
import com.wlwl.utils.StrFormat;

public class ProtocolMessgeForGuoBiao implements IProtocolAnalysis, Serializable, Cloneable {

	private String Protocol = "CD039E17A8E84137AF6DE1CDC172C274";// 协议标识，3协议的网关
	private String Node = "3CE0CF193D67408E80346E0C20263DC6";// 节点标识
	private String ProtocolForG="EF039E17A8E84137AF6DE1CDC172C274";

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

	private Map<String, VehicleInfo> _vehicles;

	public ProtocolMessgeForGuoBiao(Map<String, VehicleInfo> vehicles) {
		this._vehicles = vehicles;
	}

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

	

	public String getDeviceId() {	
		return this.gpsId;
	}

	public void setMsg(byte[] bytes) {
		this.msg=bytes;
		// 车辆VIN号
		this.gpsId = ByteUtils.bytesToAsciiString(this.msg, 4, 17);  
	}


	public String getProtocol() {
		return this.Protocol;
	}

	public byte[] answerMsg() {

		Byte commonId = this.msg[2];

		switch (commonId) {
		case (byte) 0xC0: //自定义命令，信息校对
		{
			String terminalId = ByteUtils.bytes2Str(this.msg, 24, 6);
			String ICCID = ByteUtils.bytes2Str(this.msg, 31, 20);
			String VIN = ByteUtils.bytes2Str(this.msg, 4, 17);
			VehicleInfo veh = this._vehicles.get(terminalId);
			if(veh==null)
			{
				return null;
			}
			if (veh.getVIN().equals(VIN) && veh.getICCID().equals(ICCID)) {
				return null;
			}
			byte[] tempVIN=ByteUtils.str2bytes(StrFormat.addZeroForNum(veh.getVIN(), 17));
			byte[] tempICCID=ByteUtils.str2bytes(StrFormat.addZeroForNum(veh.getICCID(), 20));
			ByteBuffer buffer = ByteBuffer.allocate(25 + 37);
			buffer.put((byte) 0x23);
			buffer.put((byte) 0x23);
			buffer.put((byte) 0xC1);
			buffer.put((byte) 0xFE);
			buffer.put(tempVIN);
			buffer.put(this.msg[21]);
			buffer.put((byte)0);
			buffer.put((byte)37);		
			buffer.put(tempVIN);
			buffer.put(tempICCID);
			buffer.put(BCCUtils.enVerbCodeForGuobiao(buffer.array()));
			return buffer.array();
		}
		case (byte) 0x01: //车辆登入
		case (byte) 0x04: //车辆登出
		case (byte) 0x07: //心跳应答
		{
			
			byte[] answer=new  byte[this.msg.length];
			answer= Arrays.copyOf(this.msg, this.msg.length);
			answer[3]=(byte)0x01;
			answer[answer.length-1]=BCCUtils.enVerbCodeForGuobiao(answer);
			return answer;
		}
		case (byte) 0x08: //校时
		{
			ByteBuffer buffer = ByteBuffer.allocate(25 + 6);
			buffer.put((byte) 0x23);
			buffer.put( (byte)0x23);
			buffer.put(commonId);
			buffer.put( (byte)0x01);
			buffer.put(ByteUtils.getSubBytes(this.msg, 4, 17));
			buffer.put(this.msg[21]);
			buffer.put((byte)0);
			buffer.put((byte) 6);
			buffer.put(ByteUtils.dateToBytes(new Date()));
			buffer.put(BCCUtils.enVerbCodeForGuobiao(buffer.array()));
			return buffer.array();
		}
		
		default:
			return null;

		}

		

	}

	/*
	 * 获取单例长度
	 * 
	 * @see com.wlwl.protocol.IProtocolAnalysis#getLength()
	 */
	// public int getLength(byte[] msg) {
	//
	// String temp = ByteUtils.byte2HexStr(this.msg);
	// int gpsLength = ByteUtils.getShort(this.msg, 3);// gps长度
	// int canLength = ByteUtils.getShort(this.msg, 7);// can 长度
	// int allLength = gpsLength + canLength;
	// return allLength;
	// }

	public int getMinLength() {
		// TODO Auto-generated method stub
		return 24;
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

	// public Boolean isMarker(byte msg) {
	// if (msg == (byte) 0x7e) {
	// return true;
	// }
	// return false;
	// }

	// public byte[] answerLogin() {
	// // TODO Auto-generated method stub
	// return null;
	// }

	public String getNode() {
		// TODO Auto-generated method stub
		return this.Node;
	}



	// 包头标志
	private static byte HEADTAG = (byte) 0x23;

	// 最小的包长
	private static byte MINPACKSIZE = (byte) 25;

	public Boolean filter(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {

		// 标记的位置
		int startPos = in.position();

		// 缓存的长度
		int packlen = in.remaining();

		// 没有接收完成，继续等待
		if (packlen < MINPACKSIZE) {
			return false;
		}

		// 包头
		byte[] headBuffer = new byte[25];
		in.get(headBuffer);

		// 查看包头
		if (headBuffer[0] != HEADTAG || headBuffer[1] != HEADTAG) {
			// System.out.println("platform recv 包头不对!");
			session.closeOnFlush();
			return true;
		}

		// 获取包长+包尾
		int contentLen = ByteUtils.getShortForLarge(headBuffer, 22);// (headBuffer,22);

		// 没有接收完全
		if (packlen < contentLen + 25) {
			in.position(startPos);
			return false;
		}

		// 没有包内容
		if (contentLen == 0) {
			out.write(headBuffer);
		} else {
			byte[] outBytes = new byte[contentLen + 25];
			System.arraycopy(headBuffer, 0, outBytes, 0, 25);
			in.get(outBytes, 25, contentLen);
			out.write(outBytes);
		}

		return true;
	}
	public void toJson(VehicleInfo vi, String ip, byte[] bytes, BlockingQueue<ProtocolModel> _sendQueue) {
		// TODO Auto-generated method stub
		ProtocolModel pm = new ProtocolModel();
		pm.setDEVICE_ID(vi.getDEVICE_ID());
		pm.setCELLPHONE(vi.getCELLPHONE());

		pm.setNode_unid(getNode());
		pm.setUnid(vi.getUNID());
		if(bytes[2]==(byte)0xD0)
		{
			int dataL=ByteUtils.getShortForLarge(bytes, 22);
			byte[] dataTemp=ByteUtils.getSubBytes(bytes, 24, dataL);
			pm.setProto_unid(getProtocol());
			pm.setRAW_OCTETS(ByteUtils.bytesToHexString(dataTemp));
			pm.setLength(String.valueOf(dataTemp.length / 2));
		}
		else
		{
			pm.setProto_unid(ProtocolForG);
			pm.setRAW_OCTETS(ByteUtils.bytesToHexString(bytes));
			pm.setLength(String.valueOf(pm.getRAW_OCTETS().length() / 2));	
		}
		pm.setTIMESTAMP(new Date().getTime());
		pm.setIP4(ip);
		try {
			_sendQueue.put(pm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
