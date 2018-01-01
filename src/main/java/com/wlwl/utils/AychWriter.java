//package com.wlwl.utils;
//
//public class AychWriter extends Thread {
//	private String content;
//	private String fileName;
//	String userUrl = System.getProperty("user.dir");
//
//	public AychWriter(String content, String fileName) {
//		this.content = content;
//		this.fileName = userUrl + "/logs/" + fileName.trim()+".log";
//	}
//
//	@Override
//	public void run() {
//		// System.out.println("开始执行run()");
//		LogWriter logger = null;
//		// String fileName = "d:/temp/logger.log";
//		// long startTime=System.currentTimeMillis();
//		try {
//			logger = LogWriter.getLogWriter(fileName);
//			logger.log(this.content);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		// long endTime=System.currentTimeMillis();
//		// System.out.println("总消耗时间："+(endTime-startTime));
//	}
//}
