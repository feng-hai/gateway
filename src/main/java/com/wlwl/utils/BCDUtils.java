package com.wlwl.utils;

import java.util.Calendar;
import java.util.Date;

public class BCDUtils {
	
	
	/** 
     * 功能: BCD码转为10进制串(阿拉伯数据) 
     * 参数: BCD码 
     * 结果: 10进制串 
     */  
    public static String bcd2Str(byte[] bytes) {  
        StringBuffer temp = new StringBuffer(bytes.length * 2);  
        for (int i = 0; i < bytes.length; i++) {  
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));  
            temp.append((byte) (bytes[i] & 0x0f));  
        }  
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp  
                .toString().substring(1) : temp.toString();  
    }  
  
    /** 
     * 功能: 10进制串转为BCD码 
     * 参数: 10进制串 
     * 结果: BCD码 
     */  
    public static byte[] str2Bcd(String asc) {  
        int len = asc.length();  
        int mod = len % 2;  
        if (mod != 0) {  
            asc = "0" + asc;  
            len = asc.length();  
        }  
        byte abt[] = new byte[len];  
        if (len >= 2) {  
            len = len / 2;  
        }  
        byte bbt[] = new byte[len];  
        abt = asc.getBytes();  
        int j, k;  
        for (int p = 0; p < asc.length() / 2; p++) {  
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {  
                j = abt[2 * p] - '0';  
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {  
                j = abt[2 * p] - 'a' + 0x0a;  
            } else {  
                j = abt[2 * p] - 'A' + 0x0a;  
            }  
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {  
                k = abt[2 * p + 1] - '0';  
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {  
                k = abt[2 * p + 1] - 'a' + 0x0a;  
            } else {  
                k = abt[2 * p + 1] - 'A' + 0x0a;  
            }  
            int a = (j << 4) + k;  
            byte b = (byte) a;  
            bbt[p] = b;  
        }  
        return bbt;  
    }  
    
    public static byte[] dateToBytes(Date d) throws NullPointerException
    {
        if (d == null)
        {
            throw new NullPointerException("Null Date value.");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        byte[] result = new byte[6];
        result[0] = (byte) (c.get(Calendar.YEAR) - 2000);
        result[1] = (byte) (c.get(Calendar.MONTH) + 1);
        result[2] = (byte) c.get(Calendar.DAY_OF_MONTH);
        result[3] = (byte) c.get(Calendar.HOUR_OF_DAY);
        result[4] = (byte) c.get(Calendar.MINUTE);
        result[5] = (byte) c.get(Calendar.SECOND);
        c = null;
        return result;
    }

}
