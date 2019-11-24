package fr.mdales.ordereventsourcing;

public class Item {
    private final String name;
    private final Double price;

    public Item(String name, double price){
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }
}

