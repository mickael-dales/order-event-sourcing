package fr.mdales.ordereventsourcing;

public class DeliveryModeChosenEvent implements OrderEvent {

    private final int orderId;
    private final DeliveryMode deliveryMode;

    public DeliveryModeChosenEvent(int orderId, DeliveryMode deliveryMode) {
        this.orderId = orderId;
        this.deliveryMode = deliveryMode;
    }

    @Override
    public Order apply(Order order) {
        order.apply(this);
        return order;
    }

    @Override
    public int getOrderId() {
        return orderId;
    }

    public DeliveryMode getDeliveryMode() {
        return deliveryMode;
    }
}
