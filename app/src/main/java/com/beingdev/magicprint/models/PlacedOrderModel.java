package com.beingdev.magicprint.models;

/**
 * Created by kshitij on 22/1/18.
 */

public class PlacedOrderModel {

    private String orderid,no_of_items,total_amount,delivery_date,payment_mode,deleiveryname,deliveryemail,deliverymobile_no,deliveryaddress,deliverypincode;
    private String placed_user_name,Placed_user_email,Placed_user_mobile_no;

    public PlacedOrderModel(String orderid, String no_of_items, String total_amount, String delivery_date, String payment_mode, String deleiveryname, String deliveryemail, String deliverymobile_no, String deliveryaddress, String deliverypincode, String placed_user_name, String getPlaced_user_email, String getPlaced_user_mobile_no) {

        this.orderid = orderid;
        this.no_of_items = no_of_items;
        this.total_amount = total_amount;
        this.delivery_date = delivery_date;
        this.payment_mode = payment_mode;
        this.deleiveryname = deleiveryname;
        this.deliveryemail = deliveryemail;
        this.deliverymobile_no = deliverymobile_no;
        this.deliveryaddress = deliveryaddress;
        this.deliverypincode = deliverypincode;
        this.placed_user_name = placed_user_name;
        this.Placed_user_email = getPlaced_user_email;
        this.Placed_user_mobile_no = getPlaced_user_mobile_no;
    }


    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPlaced_user_email() {
        return Placed_user_email;
    }

    public void setPlaced_user_email(String placed_user_email) {
        Placed_user_email = placed_user_email;
    }

    public String getPlaced_user_mobile_no() {
        return Placed_user_mobile_no;
    }

    public void setPlaced_user_mobile_no(String placed_user_mobile_no) {
        Placed_user_mobile_no = placed_user_mobile_no;
    }

    public String getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(String no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getDeleiveryname() {
        return deleiveryname;
    }

    public void setDeleiveryname(String deleiveryname) {
        this.deleiveryname = deleiveryname;
    }

    public String getDeliveryemail() {
        return deliveryemail;
    }

    public void setDeliveryemail(String deliveryemail) {
        this.deliveryemail = deliveryemail;
    }

    public String getDeliverymobile_no() {
        return deliverymobile_no;
    }

    public void setDeliverymobile_no(String deliverymobile_no) {
        this.deliverymobile_no = deliverymobile_no;
    }

    public String getDeliveryaddress() {
        return deliveryaddress;
    }

    public void setDeliveryaddress(String deliveryaddress) {
        this.deliveryaddress = deliveryaddress;
    }

    public String getDeliverypincode() {
        return deliverypincode;
    }

    public void setDeliverypincode(String deliverypincode) {
        this.deliverypincode = deliverypincode;
    }

    public String getPlaced_user_name() {
        return placed_user_name;
    }

    public void setPlaced_user_name(String placed_user_name) {
        this.placed_user_name = placed_user_name;
    }

}
