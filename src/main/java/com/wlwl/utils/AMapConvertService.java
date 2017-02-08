package com.wlwl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AMapConvertService {
	
	
	private static String targetURL = "http://restapi.amap.com/v3/assistant/coordinate/convert?key=f25e2512162cf4114dba38fec6faab61&locations={0}&coordsys=gps";
	public static String[] getConvert(String lng,String lat)
	{
		String[] possion=new String[2];
		String temp =targetURL.replace("{0}", lng+","+lat);
		
		try {

            URL restServiceURL = new URL(temp);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");

            if (httpConnection.getResponseCode() != 200) {
                   throw new RuntimeException("HTTP GET Request Failed with Error code : "
                                 + httpConnection.getResponseCode());
            }

            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                   (httpConnection.getInputStream())));

            String output;
            //System.out.println("Output from Server:  \n");
            StringBuilder sb=new StringBuilder();
            while ((output = responseBuffer.readLine()) != null) {
                   sb.append(output);
            }
            System.out.println(sb.toString());
            

            httpConnection.disconnect();
           

       } catch (MalformedURLException e) {

            e.printStackTrace();

       } catch (IOException e) {

            e.printStackTrace();

       }
		
		return possion;
	}

  //  private static final String targetURL = "http://restapi.amap.com/v3/assistant/coordinate/convert?key=f25e2512162cf4114dba38fec6faab61&locations={0}&coordsys=gps";
    public static void main(String[] args) {
    	getConvert("116.481499","39.99047");
      }
}

