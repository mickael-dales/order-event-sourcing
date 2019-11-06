package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;

import java.util.Random;

public class Order {
    private final EventStore eventStore;
    private boolean created;
    private int id;

    public Order(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public Order(EventStore eventStore, int id) {
        this.eventStore = eventStore;
        this.id = id;
        this.created = eventStore.getEvents().stream().anyMatch(event -> event instanceof OrderCreatedEvent && event.getId() == id);
    }

    public void create() {
        created = true;
        id = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(id));
    }

    public void chooseDeliveryMode() {
        if (!isCreated()) {
            throw new CannotChooseDeliveryModeOnNotCreatedOrder();
        }
        eventStore.add(new DeliveryModeChosenEvent());
    }

    private boolean isCreated() {
        return created;
    }
}
