package com.fpmi.vladcord.ui.messages_list.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fpmi.vladcord.MainActivity;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences preferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences1.getString("current_user", "none");
        SharedPreferences preferences2 = getSharedPreferences("PREFSSTATUS", MODE_PRIVATE);
        String currentStatus = preferences2.getString("current_status", "none");

        String user = remoteMessage.getData().get("user");
        String sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(currentUser);
        System.out.println(sented);
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            if (!currentUser.equals(user) && currentStatus.equals(getString(R.string.mute_friend))) {
                sendNotification(remoteMessage);
            }

        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("friendId", user);
        intent.putExtra("friendName", title);

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                j, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel
                    (title
                            , title
                            , NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(this, title)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(PRIORITY_HIGH);


        int i = 0;
        if (j > 0) {
            i = j;
        }
        notificationManager.notify(
                i, notificationBuilder.build());

    }
}
