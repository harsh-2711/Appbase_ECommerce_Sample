package com.beingdev.magicprint.notification;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.beingdev.magicprint.MainActivity;
import com.beingdev.magicprint.NotificationActivity;
import com.beingdev.magicprint.init.CustomApplication;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by kshitij on 6/1/18.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String activityToBeOpened;


        //While sending a Push notification from OneSignal dashboard
        // you can send an addtional data named "activityToBeOpened" and retrieve the value of it and do necessary operation
        //If key is "activityToBeOpened" and value is "AnotherActivity", then when a user clicks
        //on the notification, AnotherActivity will be opened.
        //Else, if we have not set any additional data MainActivity is opened.
        if (data != null) {
            activityToBeOpened = data.optString("activityToBeOpened", null);
            if (activityToBeOpened != null && activityToBeOpened.equals("NotificationActivity")) {
                Log.i("OneSignalExample", "customkey set with value: " + activityToBeOpened);
                Intent intent = new Intent(CustomApplication.getContext(), NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                CustomApplication.getContext().startActivity(intent);
            } else if (activityToBeOpened != null && activityToBeOpened.equals("MainActivity")) {
                Log.i("OneSignalExample", "customkey set with value: " + activityToBeOpened);
                Intent intent = new Intent(CustomApplication.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                CustomApplication.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(CustomApplication.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                CustomApplication.getContext().startActivity(intent);
            }

        }

        //If we send notification with action buttons we need to specify the button id's and retrieve it to
        //do the necessary operation.
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
            if (result.action.actionID.equals("ActionOne")) {
                Toast.makeText(CustomApplication.getContext(), "ActionOne Button was pressed", Toast.LENGTH_LONG).show();
            } else if (result.action.actionID.equals("ActionTwo")) {
                Toast.makeText(CustomApplication.getContext(), "ActionTwo Button was pressed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
