package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.domain.DeliveryMode;
import fr.mdales.ordereventsourcing.domain.Order;

public class DeliveryModeChanged implements OrderEvent {
    private final int orderId;
    private final DeliveryMode deliveryMode;

    public DeliveryModeChanged(int id, DeliveryMode deliveryMode) {
        this.orderId = id;
        this.deliveryMode = deliveryMode;
    }

    @Override
    public int getOrderId() {
        return orderId;
    }

    public DeliveryMode getDeliveryMode() {
        return deliveryMode;
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
