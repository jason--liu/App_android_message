package com.liu.jason.android_message;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private Button button = null;
    private final String TAG = "MessageTest";
    private int ButtonCount = 0;
    private Thread myThread = null;
    private Mythread mythread2 = null;
    private Handler mHandler = null;
    private Handler mHandler3 = null;
    private int mMessagecount = 0;
    private HandlerThread myThread3 = null;
    /**
     * @author: jason
     * @Description:
     * @version:
     * @date: 17-8-27 下午6:59
     */
    class MyRunable implements Runnable {
        int count = 0;
        @Override
        public void run() {
            for (;;) {
                Log.d(TAG, "Mythread "+count);
                count++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author: jason
     * @Description:
     * @version:
     * @date: 17-8-27 下午7:14
     */
    class Mythread extends Thread {
        private Looper mLooper;
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            synchronized (this) {
                mLooper = Looper.myLooper();
                notifyAll();
            }
            Looper.loop();
        }
        public Looper getLooper() {
            if (!isAlive()) {
                return null;
            }

            // If the thread has been started, wait until the looper has been created.
            synchronized (this) {
                while (isAlive() && mLooper == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return mLooper;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.d(TAG, "Send Message "+ButtonCount);
                ButtonCount++;
                Message mesg = new Message();
                mHandler.sendMessage(mesg);
                mHandler3.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "getMessage for Thread3 "+mMessagecount);
                    }
                });
            }
        });

        myThread = new Thread(new MyRunable(), "MessageTestThread");
        myThread.start();

        mythread2 = new Mythread();
        mythread2.start();

        /* 将handler和looper绑定 */
        mHandler = new Handler(mythread2.getLooper(), new Handler.Callback() {
            @Override
            /* 消息处理函数 */
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "getMessage "+mMessagecount);
                mMessagecount++;
                return false;
            }

        });
        myThread3 = new HandlerThread("MessageTestThread3");
        myThread3.start();

           /* 将handler和looper绑定 */
        mHandler3 = new Handler(myThread3.getLooper());
    }


}
