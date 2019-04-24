package com.konka.renting.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.konka.renting.R;
import com.konka.renting.landlord.MainActivity;

/**
 * Created by naterobinson on 2018/3/9.
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";

    public NotificationUtils(Context context){
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String content){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("is_go_to_message", true);
        mainIntent.putExtra("title",title);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true);
    }
    public NotificationCompat.Builder getNotification_25(String title, String content){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("is_go_to_message", true);
        mainIntent.putExtra("title",title);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true);
    }
    public void sendNotification(String title, String content){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (title, content).build();
            getManager().notify(1,notification);
        }else{
            Notification notification = getNotification_25(title, content).build();
            getManager().notify(1,notification);
        }
    }
}
