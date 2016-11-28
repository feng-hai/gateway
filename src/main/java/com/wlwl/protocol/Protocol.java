package com.wlwl.protocol;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IoSession;

import com.wlwl.filter.IFilterControl;
import com.wlwl.model.ProtocolModel;
import com.wlwl.model.VehicleInfo;
import com.wlwl.one.IServerHandler;
import com.wlwl.utils.ByteUtils;
import com.wlwl.utils.JsonUtils;

/**
 * @author FH
 *
 */
public class Protocol implements IFilterControl, IServerHandler {

	private IProtocolAnalysis analysis;
	BlockingQueue<ProtocolModel> _sendQueue;
	private  List<VehicleInfo> _vehicles;

	public Protocol(int port, IProtocolAnalysis _analysis, BlockingQueue<ProtocolModel> sendQueue,
			List<VehicleInfo> vehicles) {
		this.setPort(port);
		this.analysis = _analysis;
		this._sendQueue = sendQueue;
		this._vehicles = vehicles;

	}

	private int port = 9015;
	private byte[] msg;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlwl.one.IServerHandler#getDeviceId() 获取终端id
	 */
	public String getDeviceId() {
		return analysis.getDeviceId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlwl.filter.IFilterControl#setMsg(byte[]) 设置二进制码流
	 */
	public void setMsg(byte[] msg) {
		analysis.setMsg(msg);
		this.msg = msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlwl.one.IServerHandler#getPort()
	 */
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	// 尾部校验是否通过
	public Boolean checkRight() {
		return analysis.checkRight();
	}

	// 检查报的长度和头部文件的长度是否匹配
	public Boolean checkLength() {

		return analysis.checkLength();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlwl.one.IServerHandler#answerLogin(org.apache.mina.core.session.
	 * IoSession) 验证是否是登录数据，如果是登录数据，返回true，否则返回false
	 */
	public Boolean answerLogin(IoSession session) {
		// TODO Auto-generated method stub

		// String msgRe = new RegisterReMsg().createDocument();

		// session.write(msgRe);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlwl.one.IServerHandler#heartMsg(org.apache.mina.core.session.
	 * IoSession) 验证是否是心跳数据，如果是心跳数据返回true，并给出想要， 否则返回false
	 */
	public Boolean answerMsg(IoSession session) {
		// TODO Auto-generated method stub
		return this.analysis.answerMsg(session);
	}

	// 验证终端的合法性
	public VehicleInfo checkLegitimacy() {

		String deviceId = this.analysis.getDeviceId();

		synchronized (this._vehicles) {
			Iterator<VehicleInfo> it = this._vehicles.iterator();
			while (it.hasNext()) {
				VehicleInfo vi = it.next();
				if (this.analysis.getProtocol().equals("808")) {
					if (vi.getCELLPHONE().equals(deviceId)) {
						return vi;
					}
				} else {
					if (vi.getDEVICE_ID().equals(deviceId)) {
						return vi;
					}
				}
			}
			return null;
		}
		// return true;
	}

	public void toJson(VehicleInfo vi, IoSession session) {
		ProtocolModel pm = new ProtocolModel();
		// VehicleInfo vi=this._vehicles.

		pm.setDEVICE_ID(vi.getDEVICE_ID());
		pm.setProto_unid(vi.getROOT_PROTO_UNID());
		pm.setUnid(vi.getUNID());
		pm.setRAW_OCTETS(ByteUtils.bytesToHexString(this.msg));
		pm.setLength(String.valueOf(pm.getRAW_OCTETS().length() / 2));
		pm.setTIMESTAMP(new Date().getTime());
		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		pm.setIP4(clientIP);
		// System.out.println("============ server recive msg
		// ===================");
		// System.out.println(JsonUtils.serialize(pm));

		try {
			this._sendQueue.put(pm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public Boolean isHeader() {
		return analysis.isHeader();
	}

	public Boolean isEnd() {

		return analysis.isEnd();
	}

}
