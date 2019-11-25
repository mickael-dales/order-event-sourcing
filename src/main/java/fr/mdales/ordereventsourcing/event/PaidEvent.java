package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.Order;

public class PaidEvent implements OrderEvent {
    private final int orderId;
    private final double amount;

    public PaidEvent(int orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    @Override
    public int getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public Order apply(Order order) {
        order.apply(this);
        return order;
    }
}
