package com.example.clientorder.ObjectsAndAdapters;

public class Products {

    public int id;
    public String name;
    public boolean availability;
    public double price;
    public String description;
    public String type;
    public String nameENG;
    public String descriptionENG;

    public Products() {
    }

    public Products(int id, String name, boolean availability, double price, String description, String type, String nameENG, String descriptionENG) {
        this.id = id;
        this.name = name;
        this.availability = availability;
        this.price = price;
        this.description = description;
        this.type = type;
        this.nameENG = nameENG;
        this.descriptionENG = descriptionENG;
    }

}

