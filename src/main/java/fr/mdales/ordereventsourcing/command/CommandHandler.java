package fr.mdales.ordereventsourcing.command;

import fr.mdales.ordereventsourcing.domain.Basket;
import fr.mdales.ordereventsourcing.domain.DeliveryMode;
import fr.mdales.ordereventsourcing.domain.Item;
import fr.mdales.ordereventsourcing.domain.Order;
import fr.mdales.ordereventsourcing.event.EventDispatcher;
import fr.mdales.ordereventsourcing.event.OrderEvent;
import fr.mdales.ordereventsourcing.event.OrderEventStore;
import org.springframework.stereotype.Service;

@Service
public class CommandHandler {

    private final OrderEventStore orderEventStore;
    private final EventDispatcher eventDispatcher;

    public CommandHandler(OrderEventStore orderEventStore, EventDispatcher eventDispatcher) {
        this.orderEventStore = orderEventStore;
        this.eventDispatcher = eventDispatcher;
    }

    public Integer handle(CreateOrderCommand createOrderCommand) {
        Order order = new Order(orderEventStore);
        Basket basket = new Basket();
        createOrderCommand.getItems().forEach(item -> basket.addItem(new Item(item.getName(), item.getPrice())));
        OrderEvent event = order.create(basket);
        orderEventStore.add(event);
        event.apply(order);
        event.dispatch(eventDispatcher);
        return event.getOrderId();
    }

    public void handle(ChooseDeliveryModeCommand chooseDeliveryModeCommand) {
        Order order = orderEventStore.getOrder(chooseDeliveryModeCommand.getOrderId());
        OrderEvent event = order.chooseDeliveryMode(new DeliveryMode(chooseDeliveryModeCommand.getName(), chooseDeliveryModeCommand.getPrice()));
        orderEventStore.add(event);
        event.apply(order);
        event.dispatch(eventDispatcher);
    }
}
