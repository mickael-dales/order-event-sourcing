package fr.mdales.ordereventsourcing;

public interface OrderEvent {

    int getOrderId();

    Order apply(Order order);
}
