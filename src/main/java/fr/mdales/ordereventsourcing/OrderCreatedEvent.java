package fr.mdales.ordereventsourcing;

public class OrderCreatedEvent implements OrderEvent {
    private int orderId;

    public OrderCreatedEvent(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public int getId() {
        return orderId;
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
}
