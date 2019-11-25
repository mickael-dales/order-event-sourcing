package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.event.*;
import fr.mdales.ordereventsourcing.exception.CannotAddOrRemoveItemOnPaidOrder;
import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;
import fr.mdales.ordereventsourcing.exception.CannotDeleteNotSetDeleveryMode;
import fr.mdales.ordereventsourcing.exception.CannotPaidOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Order {
    private final OrderEventStore eventStore;
    private Integer id;
    private final List<Item> items = new ArrayList<>();
    private DeliveryMode deliveryMode;
    private double amount = 0;
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

    public void changeDeliveryMode(DeliveryMode deliveryMode) {
        if (this.deliveryMode == null) {
            throw new CannotDeleteNotSetDeleveryMode();
        }
        OrderEvent event = new DeliveryModeChanged(id, deliveryMode);
        event.apply(this);
        eventStore.add(event);
    }

    public void addItem(Item item) {
        if (isPaid()) {
            throw new CannotAddOrRemoveItemOnPaidOrder();
        }
        OrderEvent event = new ItemAdded(id, item);
        event.apply(this);
        eventStore.add(event);
    }

    public void removeItem(Item item) {
        if (isPaid()) {
            throw new CannotAddOrRemoveItemOnPaidOrder();
        }
        OrderEvent event = new ItemRemoved(id, item);
        event.apply(this);
        eventStore.add(event);
    }

    public void apply(OrderCreatedEvent event) {
        id = event.getOrderId();
        items.addAll(event.getItems());
        amount = items.stream().mapToDouble(Item::getPrice).sum();
    }

    public void apply(DeliveryModeChosenEvent event) {
        deliveryMode = event.getDeliveryMode();
        amount += event.getDeliveryMode().getPrice();
    }

    public void apply(PaidEvent event) {
        paid = true;
    }

    public List<Item> getItems() {
        return items;
    }

    public double getAmount() {
        return amount;
    }

    public void apply(DeliveryModeChanged event) {
        if (deliveryMode != null) {
            amount -= deliveryMode.getPrice();
        }
        deliveryMode = event.getDeliveryMode();
        amount += event.getDeliveryMode().getPrice();
    }

    public void apply(ItemAdded event) {
        items.add(event.getItem());
        amount += event.getItem().getPrice();
    }


    public void apply(ItemRemoved event) {
        items.remove(event.getItem());
        amount -= event.getItem().getPrice();
    }
}