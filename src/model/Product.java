package model;

public class Product {
    private int id;
    private int size;
    private String color;
    private String brand;
    private int price;
    private int stock;

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }

    public int getPrice() {
        return price;
    }


    public Product(int id, int size, String color, String brand, int price, int stock) {
        this.id = id;
        this.size = size;
        this.color = color;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }


}

