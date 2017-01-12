package com.wlwl.utils;

public class BCCUtils {
	 /**
     * 生成CRC包尾
     * @param data
     * @return
     */
    public static byte enVerbCode(byte[] data)
    {

        byte y = data[1];
        for (int i = 2; i < data.length - 2; i++)
        {
            y ^= data[i];
        }
        return y;
    }

}
