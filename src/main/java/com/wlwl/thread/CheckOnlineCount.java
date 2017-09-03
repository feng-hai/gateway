package com.wlwl.thread;

import com.wlwl.one.SessionManager;
import com.wlwl.utils.publicStaticMap;

public class CheckOnlineCount extends Thread {
	


	@Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {   
            try {
            	System.out.println(publicStaticMap.getSessionManager().getCount());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupt");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("thread stop");
    }

    public void cancel() {
        interrupt();
    }

}
