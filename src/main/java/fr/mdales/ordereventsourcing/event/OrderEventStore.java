package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.domain.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderEventStore {

    private final List<OrderEvent> events = new ArrayList<>();

    public List<OrderEvent> getEvents() {
        return events;
    }

    public void add(OrderEvent orderEvent) {
        events.add(orderEvent);

    }

    public Order getOrder(int id) {
        Order order = new Order(this, id);
        events.stream().filter(event -> event.getOrderId() == id).
                forEachOrdered(orderEvent -> orderEvent.apply(order));
        return order;
    }
}
