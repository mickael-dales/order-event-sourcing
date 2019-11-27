package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.domain.Item;
import fr.mdales.ordereventsourcing.domain.Order;

public class ItemRemoved implements OrderEvent{
    private final int orderId;
    private final Item item;

    public ItemRemoved(int orderId, Item item) {
        this.orderId = orderId;
        this.item = item;
    }

    @Override
    public int getOrderId() {
        return orderId;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Order apply(Order order) {
        order.apply(this);
        return order;
    }

    @Override
    public void dispatch(EventDispatcher eventDispatcher) {

    }
}
