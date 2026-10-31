package com.example.test3.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.test3.models.StudySchedule;
import com.example.test3.receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    public static final String CHANNEL_ID = "study_schedule_channel";
    private static final String CHANNEL_NAME = "Lịch học & Thông báo";
    private static final String CHANNEL_DESC = "Kênh thông báo nhắc nhở buổi học và thay đổi lịch học";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static void sendNotification(Context context, String title, String message) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            int notificationId = (int) System.currentTimeMillis();
            manager.notify(notificationId, builder.build());
        }
    }

    public static void scheduleUpcomingAlarm(Context context, StudySchedule schedule) {
        try {
            String dateTimeStr = schedule.getStudyDate() + " " + schedule.getStartTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(dateTimeStr);

            if (date != null) {
                long triggerTime = date.getTime() - (15 * 60 * 1000); // 15 mins before

                if (triggerTime > System.currentTimeMillis()) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmReceiver.class);

                    String subjectName = schedule.getSubjectName() != null ? schedule.getSubjectName() : "Môn học";
                    intent.putExtra("EXTRA_TITLE", "Nhắc nhở buổi học sắp tới!");
                    intent.putExtra("EXTRA_MESSAGE", "Môn " + subjectName + " sẽ bắt đầu lúc " + schedule.getStartTime() + " tại " + schedule.getLocation());

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context,
                            schedule.getId(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    if (alarmManager != null) {
                        try {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                        } catch (SecurityException se) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
