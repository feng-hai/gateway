package com.wlwl.protocol.Packages;

public class LockBody {
	
	//经度
	private Integer longitude;
	
	//纬度
	private Integer latitude;
	
	//锁车类型
	private byte lockType;
	
	//锁车剩余时间
	private short leftTime;

	public Double getLongitude() {
		return longitude/1000000.0;
	}

	public void setLongitude(Integer longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude/1000000.0;
	}

	public void setLatitude(Integer latitude) {
		this.latitude = latitude;
	}

	public byte getLockType() {
		return lockType;
	}

	public void setLockType(byte lockType) {
		this.lockType = lockType;
	}

	public short getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(short leftTime) {
		this.leftTime = leftTime;
	}
}
