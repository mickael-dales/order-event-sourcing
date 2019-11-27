package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.domain.Order;

public interface OrderEvent {

    int getOrderId();

    Order apply(Order order);

    void dispatch(EventDispatcher eventDispatcher);
}
