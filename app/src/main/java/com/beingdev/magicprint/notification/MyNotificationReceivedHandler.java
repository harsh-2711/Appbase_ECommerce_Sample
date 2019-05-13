package com.beingdev.magicprint.notification;

import android.util.Log;

import com.beingdev.magicprint.db.Notification;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by kshitij on 6/1/18.
 */

public class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;

        String message = notification.payload.body;
        String header = notification.payload.title;

        //adding values of notification to the ORM ActiveAndroid
        if(message != null && header!=null) {
            Notification notificationmsg = new Notification();
            notificationmsg.title = header;
            notificationmsg.body = message;


            //add notification to db
            try{
                notificationmsg.save();
                Log.e("notification state :","notification saved !");
            }catch (Exception e){
                Log.e("error notification :",e.toString());
            }
        }

        String customKey;

        if (data != null) {
            //While sending a Push notification from OneSignal dashboard
            // you can send an addtional data named "customkey" and retrieve the value of it and do necessary operation
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }
    }

}
