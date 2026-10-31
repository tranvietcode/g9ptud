package com.example.test3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.test3.utils.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("EXTRA_TITLE");
        String message = intent.getStringExtra("EXTRA_MESSAGE");

        if (title == null) title = "Nhắc nhở lịch học sắp tới!";
        if (message == null) message = "Bạn có một buổi học sắp diễn ra, hãy kiểm tra lịch học ngay!";

        NotificationHelper.sendNotification(context, title, message);
    }
}
