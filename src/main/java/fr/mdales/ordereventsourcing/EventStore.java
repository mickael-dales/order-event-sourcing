package fr.mdales.ordereventsourcing;

import java.util.ArrayList;
import java.util.List;

public class EventStore {

    private final List<Event> events = new ArrayList<>();

    public List<Event> getEvents() {
        return events;
    }

    public void add(Event event) {
        events.add(event);
    }
}
