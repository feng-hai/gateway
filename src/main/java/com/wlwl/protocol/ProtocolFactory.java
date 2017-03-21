package com.wlwl.protocol;

import com.wlwl.enums.ProtocolEnum;
import com.wlwl.protocol.Packages.ProtocolMessageFor808;
import com.wlwl.protocol.Packages.ProtocolMessgeFor3G;
import com.wlwl.protocol.Packages.ProtocolMessgeForGuoBiao;
import com.wlwl.protocol.Packages.ProtocolMessgeForJinLong;

public class ProtocolFactory {
	
	public static IProtocolAnalysis getAnalysis(ProtocolEnum pEnum)
	{
		
		switch(pEnum)
		{
		case P3G:
			return new ProtocolMessgeFor3G();
		case P808:
			return new ProtocolMessageFor808();	
		case PJINLONG:
			return new ProtocolMessgeForJinLong();
		case GUOBIAO:
			return new ProtocolMessgeForGuoBiao();
		default:
			return  null;
			
		}
	}

}
