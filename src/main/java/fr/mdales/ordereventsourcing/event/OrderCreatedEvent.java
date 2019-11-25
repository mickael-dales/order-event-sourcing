package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.Item;
import fr.mdales.ordereventsourcing.Order;

import java.util.List;

public class OrderCreatedEvent implements OrderEvent {
    private int orderId;
    private final List<Item> items;

    public OrderCreatedEvent(int orderId, List<Item> items) {
        this.orderId = orderId;
        this.items = items;
    }

    @Override
    public int getOrderId() {
        return orderId;
    }

    @Override
    public Order apply(Order order) {
        order.apply(this);
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderCreatedEvent)) return false;

        OrderCreatedEvent that = (OrderCreatedEvent) o;

        return orderId == that.orderId;
    }

    @Override
    public int hashCode() {
        return orderId;
    }

    public List<Item> getItems() {
        return items;
    }
}
