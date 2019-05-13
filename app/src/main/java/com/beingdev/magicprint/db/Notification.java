package com.beingdev.magicprint.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by kshitij on 7/1/18.
 */
//This is our table name
@Table(name = "Pushnotifications")
public class Notification extends Model {

    @Column(name = "title")
    public String title;

    @Column(name = "body")
    public String body;

}
