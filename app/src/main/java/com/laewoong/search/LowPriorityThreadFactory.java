package com.laewoong.search;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class LowPriorityThreadFactory implements ThreadFactory {
    private static int count = 1;
    public static final String TAG = ThreadFactory.class.getName();
    private static final String THREAD_NAME = "LowPrio ";

    @Override
    public Thread newThread(Runnable r) {

        return newThread(r, THREAD_NAME+count++);
    }

    public Thread newThread(Runnable r,String name) {
        Thread t = new Thread(r,name);
        t.setPriority(4);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.d(TAG, "Thread = " + t.getName() + ", error = " + e.getMessage());
            }
        });

        return t;
    }

}
