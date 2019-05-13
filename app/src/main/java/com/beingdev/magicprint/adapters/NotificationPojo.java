package com.beingdev.magicprint.adapters;

/**
 * Created by kshitij on 15/1/18.
 */

public class NotificationPojo {

    String title;
    String body;

    public NotificationPojo(String title,String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
