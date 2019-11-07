package fr.mdales.ordereventsourcing;

public class PaidEvent implements OrderEvent {
    private final int orderId;

    public PaidEvent(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public int getId() {
        return orderId;
    }

    @Override
    public Order apply(Order order) {
        order.apply(this);
        return order;
    }
}
