package com.wlwl.protocol.Packages;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.protocol.IProtocolAnalysis;
import com.wlwl.utils.BCDUtils;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.CRCUtil;

/**
 * @author FH
 *
 */
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
	 * // 终端唯一标识，AF27DA9036174426A2E2F7C19A9A959C 是用手机号码作为终端的唯一标识的
	 */
	public String getDeviceId() {
		
		
		
		  //第五位是手机号码的开始位
		byte[] telephone=ByteUtils.getSubBytes(this.msg, 5, 6);
		  
		String teleStr=BCDUtils.bcd2Str(telephone);
		
		  return teleStr;
	}

	public void setMsg(byte[] bytes) {
		this.msg = descape(bytes);
		this.commandId=ByteUtils.getShortForLarge(this.msg, 1);
		this.serialNumber=ByteUtils.getShortForLarge(this.msg, 11);
	}

	public String getProtocol() {
		return this.Protocol;
	}
	public byte[] answerMsg() {
		byte[] answerBytes ;
		switch (this.commandId) {
		case (short) 0x0100:// 注册
		{
			answerBytes = new byte[19];
			answerBytes[0] = 0x7e;// 标识符
			answerBytes[answerBytes.length - 1] = 0x7e;// 标识符
			// 命令ID
			answerBytes[1] = (byte) 0x81;
			answerBytes[2] = (byte) 0x00;
			
			// 设置信息体长度
			answerBytes[4] = (byte) 4;
			// 设置手机号码
			for (int i = 5; i < 11; i++) {
				answerBytes[i] = this.msg[i];
			}
			// 设置消息体
			// 应答流水号-》终端上传流水号
             short temp=(short)(this.serialNumber+1);
             answerBytes[11] = (byte) (temp>> 8);
 			answerBytes[12] = (byte) temp ;
			answerBytes[13] = (byte) (this.serialNumber>> 8);
			answerBytes[14] = (byte) this.serialNumber ;

			// 应答id -》终端上传命令id

//			answerBytes[15] = (byte) (this.commandId>> 8);
//			answerBytes[16] = (byte) (this.commandId );

			answerBytes[15] = (byte) 0;//0：成功/确认；1：失败；2：消息有误；3：不支持
			answerBytes[16] = (byte) 10;//鉴权码
			// 设置验证码
			answerBytes[17] = CRCUtil.crc808(answerBytes);
			break;
		}
		case (short) 0x0102:
		case (short) 0x0200:
		case (short) 0x0705:
		case (short) 0x0002:// 心跳
		{
			answerBytes = new byte[20];
			answerBytes[0] = 0x7e;// 标识符
			answerBytes[answerBytes.length - 1] = 0x7e;// 标识符
			// 命令ID
			answerBytes[1] = (byte) 0x80;
			answerBytes[2] = (byte) 0x01;
			
			// 设置信息体长度
			answerBytes[4] = (byte) 5;
			// 设置手机号码
			for (int i = 5; i < 11; i++) {
				answerBytes[i] = this.msg[i];
			}
			// 设置消息体
			// 应答流水号-》终端上传流水号
			short temp=(short)(this.serialNumber+1);
			answerBytes[11] = (byte) (temp>> 8);
			answerBytes[12] = (byte) temp ;
			answerBytes[13] = (byte) (this.serialNumber>> 8);
			answerBytes[14] = (byte) this.serialNumber ;

			// 应答id -》终端上传命令id

			answerBytes[15] = (byte) (this.commandId>> 8);
			answerBytes[16] = (byte) (this.commandId );

			answerBytes[17] = (byte) 0;//0：成功/确认；1：失败；2：消息有误；3：不支持
			
			// 设置验证码
			answerBytes[18] = CRCUtil.crc808(answerBytes);
			break;
		}
		case (short)0x0003://终端注销
		{
			
		}
		default:
			return null;
		}
		//session.write(escape(answerBytes));

		return escape(answerBytes);
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
   
	/* 消息体长度
	 * @see com.wlwl.protocol.IProtocolAnalysis#getLength(byte[])
	 */
	public int getLength(byte[] msg) {
		short length=ByteUtils.getShort(msg, 3);
		return length;
	}
     
	/* 最小长度
	 * @see com.wlwl.protocol.IProtocolAnalysis#getMinLength()
	 */
	public int getMinLength() {
		// TODO Auto-generated method stub
		return 16;
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

	public byte[] answerLogin() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final byte OX7D = 0x7d;

	private static final byte[] OX7D_ESCAPE = { 0x7d, 0x01 };

	public static final byte OX7E = 0x7e;

	private static final byte[] OX7E_ESCAPE = { 0x7d, 0x02 };

	/**
	 * 还原转义
	 */
	protected byte[] descape(byte[] octets) {
		if (octets != null && octets.length > 2) {
			ByteBuffer buffer = ByteBuffer.allocate(octets.length);
			buffer.put(octets[0]);// head
			int i = 1;
			for (; i < octets.length - 2; i++) {
				if (octets[i] == OX7D) {
					if (octets[i + 1] == OX7E_ESCAPE[1]) {
						buffer.put(OX7E);
						i++;
					} else if (octets[i + 1] == OX7D_ESCAPE[1]) {
						buffer.put(OX7D);
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
				if (octetsDescaped[i] == OX7E)
					buffer.put(OX7E_ESCAPE);
				else if (octetsDescaped[i] == OX7D)
					buffer.put(OX7D_ESCAPE);
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

	public Boolean filter(IoSession session,IoBuffer in,ProtocolDecoderOutput out) {
		// TODO Auto-generated method stub
		in.mark();
		int position = in.position();
		int remain = in.remaining();
		byte[] allData = new byte[remain];
		in.get(allData);
		if (!isMarker(allData[0]))// 标识符不对
		{
			session.close(true);
			//System.out.println("消息头不对：" + ByteUtils.byte2HexStr(temp));
			return false;
		}
		in.reset();
		int startIndex = -1;
		int endIndex = -1;
		boolean isFirst = true;
		for (int i = 0; i < allData.length; i++) {
			if (isMarker(allData[i])) {
				if (isFirst) {
					startIndex = i;
					in.position(i + position);
					isFirst = false;
				} else {
					endIndex = i;
				}
			}
			if (startIndex != -1 && endIndex != -1) {
				int len = endIndex - startIndex + 1;
				if (len == 2) {
					byte[] buf = new byte[1];
					in.get(buf);
					//out.write(buf);
					return true;
				} else {
					byte[] buf = new byte[len];
					in.get(buf);
					out.write(buf);
					return true;
				}
			}
			if (i >= 0xffff) {
				in.position(allData.length-1);
				session.close(true);
				System.out.println("消息体长度不匹配：" + ByteUtils.byte2HexStr(allData));
				return true;
			}
		}
		in.reset();
		return false;

	}

	public void toJson(VehicleInfo vi, String ip, byte[] bytes, BlockingQueue<ProtocolModel> _sendQueue) {
		// TODO Auto-generated method stub
		ProtocolModel pm = new ProtocolModel();
		pm.setDEVICE_ID(vi.getDEVICE_ID());
		pm.setCELLPHONE(vi.getCELLPHONE());
		pm.setProto_unid(getProtocol());
		pm.setNode_unid(getNode());
		pm.setUnid(vi.getUNID());
		pm.setRAW_OCTETS(ByteUtils.bytesToHexString(bytes));
		pm.setLength(String.valueOf(pm.getRAW_OCTETS().length() / 2));
		pm.setTIMESTAMP(new Date().getTime());
	
		pm.setIP4(ip);
		try {
			_sendQueue.put(pm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
