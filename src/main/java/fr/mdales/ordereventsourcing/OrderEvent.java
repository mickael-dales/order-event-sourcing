package fr.mdales.ordereventsourcing;

public interface OrderEvent {

    int getId();

    Order apply(Order order);
}
