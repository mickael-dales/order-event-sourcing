package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;

public class Order {

    private final EventStore eventStore;
    private boolean created;

    public Order(EventStore eventStore) {
        this.eventStore = eventStore;
        this.created = eventStore.getEvents().stream().anyMatch(event -> event instanceof OrderCreatedEvent);
    }

    public void create() {
        this.created = true;
        eventStore.add(new OrderCreatedEvent());
    }

    public void chooseDeliveryMode() {
        if (!isCreated()){
            throw new CannotChooseDeliveryModeOnNotCreatedOrder();
        }
        eventStore.add(new DeliveryModeChosenEvent());
    }

    private boolean isCreated(){
        return this.created;
    }
}
