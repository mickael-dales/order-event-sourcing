package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.Order;

public interface OrderEvent {

    int getOrderId();

    Order apply(Order order);
}
