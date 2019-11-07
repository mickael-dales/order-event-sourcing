package fr.mdales.ordereventsourcing;

public class DeliveryModeChosenEvent implements OrderEvent{

    private final int orderId;

    public DeliveryModeChosenEvent(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public Order apply(Order order) {
        order.apply(this);
        return order;
    }

    @Override
    public int getId() {
        return orderId;
    }
}
