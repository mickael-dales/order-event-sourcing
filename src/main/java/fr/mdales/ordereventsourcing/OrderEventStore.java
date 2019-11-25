package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.event.OrderEvent;

import java.util.ArrayList;
import java.util.List;

public class OrderEventStore {

    private final List<OrderEvent> events = new ArrayList<>();

    public List<OrderEvent> getEvents() {
        return events;
    }

    public void add(OrderEvent orderEvent) {
        events.add(orderEvent);
    }

    public Order getOrder(int id){
        Order order =new Order(this, id);
        events.stream().filter(event -> event.getOrderId() == id).
                forEachOrdered(orderEvent -> orderEvent.apply(order));
        return order;
    }
}
