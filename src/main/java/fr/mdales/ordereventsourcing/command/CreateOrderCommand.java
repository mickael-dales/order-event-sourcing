package fr.mdales.ordereventsourcing.command;

import java.util.List;

public class CreateOrderCommand {

    private final List<Item> items;

    public CreateOrderCommand(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
}
