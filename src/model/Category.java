package model;

public class Category {
    private final String name;
    private int id;

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }


}
