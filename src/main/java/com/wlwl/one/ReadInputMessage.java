package com.wlwl.one;

import java.util.Scanner;

public class ReadInputMessage extends Thread {
	private SessionManager manager;

	public ReadInputMessage(SessionManager _manager) {
		this.manager = _manager;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		while (true) {
			// 读取输入内容
			Scanner sc = new Scanner(System.in);
			System.out.println("请输入你要监控的内容，如需帮助请输入help：");
			String inputStr = sc.next();
			switch (inputStr) {
			case "help": {
				System.out.println("remove:清空缓存");
				System.out.println("get:获取终端id是否存在！");
				break;
			}
			case "remove": {
				this.manager.remove("211005");
				break;
			}
			case "get": {
				Boolean isTrue = this.manager.getDevice("211005");
				if (isTrue) {
					System.out.println("已存在");
				} else {
					System.out.println("不存在");
				}
			}
			default:
				System.out.println("r:清空缓存");
				break;
			}
		}
	}

}
