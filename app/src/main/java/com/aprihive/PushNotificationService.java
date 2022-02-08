// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved

package com.aprihive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "debug";
    private static final String CHANNEL_ID = "Push Notification";
    private Bitmap userImage;

    public PushNotificationService() {

        String msg = "started service";
        Log.e(TAG, "PushNotificationService: " + msg);


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "PushNotificationService: " + "message received");

        userImage = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_placeholder);


        Glide.with(this)
                .asBitmap()
                .load(remoteMessage.getData().get("senderImage"))
                .centerCrop()
                .error(R.drawable.user_image_placeholder)
                .fallback(R.drawable.user_image_placeholder)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        userImage = resource;
                        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), userImage, remoteMessage.getData().get("senderEmail"), remoteMessage.getData().get("receiverName"), 1);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        Log.e(TAG, "PushNotificationService: " + "message to notify");



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "PushNotificationService: " + "stopped service");

    }

    private void sendNotification(String title, String content, String profileImageLink, String senderEmail) {
        createNotificationChannel();

        Random random = new Random();

        String GROUP_NOTIFICATIONS = title;

        int notify_id = random.nextInt(10000);

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(PushNotificationService.this, CHANNEL_ID);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.message_notification_layout);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.content, content);

        RemoteViews remoteViewsBig = new RemoteViews(getPackageName(), R.layout.message_notification_layout_expanded);
        remoteViewsBig.setTextViewText(R.id.title, title);
        remoteViewsBig.setTextViewText(R.id.content, content);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MessagingActivity.class)
                .putExtra("getEmail", senderEmail), 0);

        builder.setSmallIcon(R.drawable.ic_message_filled).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViewsBig)
                .setContentIntent(contentIntent)
                .setGroup(GROUP_NOTIFICATIONS)
                .addAction(R.drawable.ic_message_filled, "Open Chat", contentIntent)
                .setAutoCancel(true);

        final Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(PushNotificationService.this);
        notificationManagerCompat.notify(notify_id, notification);

        Log.e(TAG, "sendNotification: done" );
        NotificationTarget notificationTarget = new NotificationTarget(PushNotificationService.this, R.id.profileImage, remoteViews, builder.build(), notify_id);


        Glide.with(this)
                .asBitmap()
                .load(profileImageLink)
                .centerCrop()
                .error(R.drawable.user_image_placeholder)
                .fallback(R.drawable.user_image_placeholder)
                .into(notificationTarget);


        NotificationTarget notificationTarget2 = new NotificationTarget(PushNotificationService.this, R.id.profileImage, remoteViewsBig, builder.build(), notify_id);

        Glide.with(this)
                .asBitmap()
                .load(profileImageLink)
                .centerCrop()
                .error(R.drawable.user_image_placeholder)
                .fallback(R.drawable.user_image_placeholder)
                .into(notificationTarget2);


        Log.e(TAG, "sendNotification: done 2" );

    }

    private void sendNotification(String title, String content, Bitmap userImageBitmap, String senderEmail, String receiverName, int id) {
        createNotificationChannel();





        Random random = new Random();


        String GROUP_NOTIFICATIONS = receiverName;
        int notify_id = random.nextInt(10000);
        int group_id = receiverName.hashCode();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MessagingActivity.class).putExtra("getEmail", senderEmail), 0);


        Notification groupBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setGroupSummary(true)
                .setStyle(new NotificationCompat.InboxStyle().setBigContentTitle("You have new messages").setSummaryText(receiverName.substring(0,1).toUpperCase() + receiverName.substring(1).toLowerCase()))
                .setGroup(GROUP_NOTIFICATIONS)
                .build();



        Notification builder  = new NotificationCompat.Builder(PushNotificationService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_filled).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setContentTitle(title)
                .setLargeIcon(getCircleBitmap(userImageBitmap))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setContentText(content)
                .setGroup(GROUP_NOTIFICATIONS)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_message_filled, "Open Chat", contentIntent)
                .build();
        
       

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(PushNotificationService.this);
        notificationManagerCompat.notify(group_id, groupBuilder);
        notificationManagerCompat.notify(notify_id, builder);
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        try {
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        


        return output;
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            CharSequence name = "Push Notify Service";
            String description = "Push notifications from server";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
