package com.linw.tomatotime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.linw.tomatotime.data.Info;
import com.linw.tomatotime.util.SharedPrefUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (status) {
                    settime(workTime);
                } else {
                    settime(restTime);
                }
            }
        }
    };

    long curTime = 0;
    long workTime ;
    long restTime ;
    boolean status = true;//true：工作  false:休息
    SharedPrefUtil sharedPrefUtil;
    TextView tvclicktime;
    TextView tvtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvtitle = (TextView) findViewById(R.id.tv_title);
        this.tvclicktime = (TextView) findViewById(R.id.tv_click_time);

        curTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        curTime = calendar.getTimeInMillis();

        sharedPrefUtil = SharedPrefUtil.getSingleInstance(this);
        String workTimeStr = sharedPrefUtil.getStringKeyVal(null, Info.StrKeyWorkTime, "");
        if (workTimeStr.isEmpty()) {
            workTime = 25 * 60 * 1000 + curTime;
        } else {
            workTime = Long.valueOf(workTimeStr);
        }

        String restTimeStr = sharedPrefUtil.getStringKeyVal(null, Info.StrKeyRestTime, "");
        if (restTimeStr.isEmpty()) {
            restTime = 5 * 60 * 1000 + curTime;
        } else {
            restTime = Long.valueOf(restTimeStr);
        }

        settime(workTime);
    }

    private void settime(long time) {
        if (time > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timeStr = sdf.format(new Date(time));
            tvclicktime.setText(timeStr);
        } else {
            timer.cancel();
            //提示到时
        }
    }

    Timer timer;

    private void startTime() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                if (status) {
                    workTime -= 1000;
                } else {
                    restTime -= 1000;
                }

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        timer.schedule(task, 1000, 1000);
    }


    public void startWorkClick(View view) {
        tvtitle.setText("工作钟");
        status = true;
        startTime();
    }

    public void startRestClick(View view) {
        tvtitle.setText("休息钟");
        status = false;
        startTime();
    }

    public void pauseClick(View view) {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

}
