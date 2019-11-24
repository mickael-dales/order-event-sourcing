package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;
import fr.mdales.ordereventsourcing.exception.CannotPaidOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Order {
    private final OrderEventStore eventStore;
    // private OrderStatusEnum status = OrderStatusEnum.NONE;
    private Integer id;
    private final List<Item> items = new ArrayList<>();
    private DeliveryMode deliveryMode;
    private boolean paid;

    public Order(OrderEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public Order(OrderEventStore eventStore, int id) {
        this.eventStore = eventStore;
        this.id = id;
    }

    public void create(Basket basket) {
        if (!this.isCreated()) {
            this.id = new Random().nextInt();
            OrderEvent event = new OrderCreatedEvent(id, basket.getItems());
            event.apply(this);
            eventStore.add(event);
        }
    }

    public void chooseDeliveryMode(DeliveryMode deliveryMode) {
        if (items.isEmpty()) {
            throw new CannotChooseDeliveryModeOnNotCreatedOrder();
        }
        OrderEvent event = new DeliveryModeChosenEvent(id, deliveryMode);
        event.apply(this);
        eventStore.add(event);
    }

    public DeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    private boolean isCreated() {
        return !items.isEmpty();
    }

    public void pay() {
        if (!isDeliveryModeChosen()) {
            throw new CannotPaidOrder();
        }
        OrderEvent event = new PaidEvent(id, getAmount());
        event.apply(this);
        eventStore.add(event);
    }

    public boolean isPaid() {
        return paid;
    }

    private boolean isDeliveryModeChosen() {
        return this.deliveryMode != null;
    }

    public void apply(OrderCreatedEvent event) {
        id = event.getOrderId();
        items.addAll(event.getItems());
    }

    public void apply(DeliveryModeChosenEvent event) {
        deliveryMode = event.getDeliveryMode();
    }

    public void apply(PaidEvent event) {
        paid = true;
    }

    public List<Item> getItems() {
        return items;
    }

    public double getAmount() {
        return items.stream().mapToDouble(Item::getPrice).sum() + getDeliveryModePrice();
    }

    private double getDeliveryModePrice() {
        if (deliveryMode == null) {
            return 0;
        }
        return deliveryMode.getPrice();
    }
}