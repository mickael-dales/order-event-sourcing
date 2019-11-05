package fr.mdales.ordereventsourcing;

public class Order {

    private final EventStore eventStore;

    public Order(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void create() {
        eventStore.add(new OrderCreatedEvent());
    }
}
