package fr.mdales.ordereventsourcing;

import java.util.ArrayList;
import java.util.List;

public class EventStore {

    private final List<OrderEvent> events = new ArrayList<>();

    public List<OrderEvent> getEvents() {
        return events;
    }

    public void add(OrderEvent orderEvent) {
        events.add(orderEvent);
    }
}
