package fr.mdales.ordereventsourcing;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private final List<Item> items =  new ArrayList<>();

    public Basket(){
    }

    public List<Item> getItems() {
        return items;
    }

    public Basket addItem(Item item){
        items.add(item);
        return this;
    }
}
