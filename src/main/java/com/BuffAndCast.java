package com;

public class BuffAndCast {
    private String id;
    private String name;
    private int price;
    private String log_type;

    public BuffAndCast(String id, String name, int price, String log_type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.log_type = log_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLogType() {
        return log_type;
    }

    public void setLogType(String log_type) {
        this.log_type = log_type;
    }

    public String getPriceName() {
        return name + "(" + price + "G)";
    }
}
