package com.example.clientorder.ObjectsAndAdapters;

public class Order {

    public String id;
    public String hour;
    public String order;
    public String Adress;
    public String PhoneNumber;
    public double cost;
    public String payment;
    public String userNameClient;
    public int points;


    public Order() {
    }

    public Order(String id, String hour, String order, String adress, String phoneNumber, double cost, String payment, String userNameClient, int points) {
        this.id = id;
        this.hour = hour;
        this.order = order;
        Adress = adress;
        PhoneNumber = phoneNumber;
        this.cost = cost;
        this.payment = payment;
        this.userNameClient = userNameClient;
        this.points = points;
    }






}

