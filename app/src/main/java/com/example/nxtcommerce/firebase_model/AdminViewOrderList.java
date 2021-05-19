package com.example.nxtcommerce.firebase_model;

public class AdminViewOrderList {
    private String name, address, date, time, mobile, status, total, userid, orderid;

    public AdminViewOrderList() {

    }

    public AdminViewOrderList(String name, String address, String date, String time, String mobile, String status, String total, String userid, String orderid) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.time = time;
        this.mobile = mobile;
        this.status = status;
        this.total = total;
        this.userid = userid;
        this.orderid = orderid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
