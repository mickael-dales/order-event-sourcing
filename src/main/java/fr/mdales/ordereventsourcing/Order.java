package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;
import fr.mdales.ordereventsourcing.exception.CannotPaidOrder;

import java.util.Random;

public class Order {
    private final OrderEventStore eventStore;
    private OrderStatusEnum status = OrderStatusEnum.NONE;
    private int id;

    public Order(OrderEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public Order(OrderEventStore eventStore, int id) {
        this.eventStore = eventStore;
        this.id = id;

    }

    public void create() {
        if (OrderStatusEnum.NONE.equals(getStatus())) {
            this.id = new Random().nextInt();
            OrderEvent event = new OrderCreatedEvent(id);
            event.apply(this);
            eventStore.add(event);
        }
    }

    public void chooseDeliveryMode() {
        if (!isCreated()) {
            throw new CannotChooseDeliveryModeOnNotCreatedOrder();
        }
        OrderEvent event = new DeliveryModeChosenEvent(id);
        event.apply(this);
        eventStore.add(new DeliveryModeChosenEvent(id));
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    private boolean isCreated() {
        return status.compareTo(OrderStatusEnum.CREATED) >= 0;
    }

    public void pay() {
        if (!isDeliveryModeChosen()) {
            throw new CannotPaidOrder();
        }
        OrderEvent event = new PaidEvent(id);
        event.apply(this);
        eventStore.add(event);
    }

    private boolean isDeliveryModeChosen() {
        return status.compareTo(OrderStatusEnum.DELIVERY_CHOSEN) >= 0;
    }

    public void apply(OrderCreatedEvent event) {
        id = event.getId();
        status = OrderStatusEnum.CREATED;
    }

    public void apply(DeliveryModeChosenEvent event) {
        status = OrderStatusEnum.DELIVERY_CHOSEN;
    }

    public void apply(PaidEvent event) {
        status = OrderStatusEnum.PAID;
    }
}
