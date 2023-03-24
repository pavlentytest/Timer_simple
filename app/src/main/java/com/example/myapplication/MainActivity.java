package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int start = 0, stop = 1, refresh = 2;

    private final Timer timer = new Timer();
    private TextView timerView;

    Handler timerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case start:
                    if (timer.isRunning())
                        break;
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        Log.d("RRR",e.fillInStackTrace().toString());
                    }
                    timer.start();
                    timerHandler.sendEmptyMessage(refresh);
                    break;
                case refresh:
                    timerView.setText(String.format("%s", format(timer.getElapsedTime())));
                    timerHandler.sendEmptyMessageDelayed(refresh, 1);
                    break;
                case stop:
                    timerHandler.removeMessages(refresh);
                    if (!timer.isRunning())
                        break;
                    timer.stop();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerView = findViewById(R.id.Time_label);
        ConstraintLayout root = findViewById(R.id.root);
        root.setOnClickListener(this::onClick);
    }

    private boolean justStopped = false, isFingerDown = false;
    private long readyTime;

    private String format(long milliseconds) {
        String time = String.format("%1$TM:%1$TS:%1$TL", milliseconds);
        return time.substring(0, time.length() - 1);
    }

    @Override
    public void onClick(View view) {
        if(!isFingerDown) {
            isFingerDown = true;
            if (timer.isRunning()) {
                timerHandler.sendEmptyMessage(stop);
                justStopped = true;
            }
            readyTime = System.currentTimeMillis();
            justStopped = false;
        } else {
            isFingerDown = false;
            if (!justStopped && System.currentTimeMillis() - readyTime > 1000) {
                timerHandler.sendEmptyMessage(start);
            }
        }
    }
}