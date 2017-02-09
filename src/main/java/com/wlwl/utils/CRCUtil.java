package com.wlwl.utils;

public class CRCUtil {
	
	 public static int parseCRCMessageTail(byte[] data)
	    {
	        int temp  =  0xFFFF;
	         for (int i = 0; i < data.length ; i++) 
	            {
	                short x = data[i];
	                if(data[i]<0)
	                    x = (short)(256-Math.abs(data[i]));
	                temp = temp ^ x;
	                for(int j = 0; j < 8; j++)
	                {
	                    if((temp & 0x0001) == 1)
	                    {
	                        temp = temp >> 1;
	                        temp = temp ^ 0x1021;
	                    }
	                    else
	                        temp = temp >> 1;
	                }
	            }
	        // ByteBuffer bf = ByteBuffer.allocate(2);
	        // bf.order(ByteOrder.LITTLE_ENDIAN);
	         //bf.putShort((short) temp);
	         return temp;
	        
	    }
	 public static final int evalCRC16(byte[] data) {
	        int crc = 0xFFFF;
	        for (int i = 0; i < data.length; i++) {
	            crc = data[i] ^ crc;
	            for (int j = 0; j < 8; ++j)
	                if ((crc & 0x0001) != 0)
	                    crc = (crc >> 1) ^ 0x1021;
	                else
	                    crc >>= 1;
	        }
	         
	        return crc;
	 }
	 
	 public static final byte crc808(byte[]data)
	 {
		 byte temp=data[1];
		 
		 for(int i=2;i<data.length-4;i++)
		 {
			 temp=(byte)(temp^data[i]);
		 }
		 return temp;
	 }
	 
	
	    public static void main(String[] args) {  
	        // 一个db44测试样本数据  
	        byte p[] = {71,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,68,31,32,0,1,2};  
	       // 29067E

			String temp="81012000000000008D0230303530303002010000C5621407C004E80100007A0033005FA40200790001011B021102080D08172F";
			
			byte[] data =ByteUtils. hexStr2Bytes(temp);
	        
	        int crc = parseCRCMessageTail(data); 
	        // 65336  
	        System.out.println(Integer.toHexString(crc));  
	          
	    }  
}
