package model;

import java.sql.Date;

public class Order {

    private int id;
    private Date orderDate;
    private int totalItems;
    private int totalSum;

    public Order(int id, Date orderDate, int totalItems, int totalSum) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalItems = totalItems;
        this.totalSum = totalSum;
    }

    public int getId() {
        return id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalSum() {
        return totalSum;
    }
}
