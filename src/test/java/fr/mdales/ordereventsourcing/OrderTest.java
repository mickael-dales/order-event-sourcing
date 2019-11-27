package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.domain.Basket;
import fr.mdales.ordereventsourcing.domain.DeliveryMode;
import fr.mdales.ordereventsourcing.domain.Item;
import fr.mdales.ordereventsourcing.domain.Order;
import fr.mdales.ordereventsourcing.event.*;
import fr.mdales.ordereventsourcing.exception.CannotAddOrRemoveItemOnPaidOrder;
import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;
import fr.mdales.ordereventsourcing.exception.CannotPaidOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JUnit4.class)
public class OrderTest {

    private static final Item CHATEAU_PIPEAU = new Item("Château Pipeau", 20);
    private static final Item TARIQUET = new Item("Tariquet", 10);
    private static final Item CHATEAU_LALOUVIERE = new Item("Château Lalouvière", 30);
    private static final DeliveryMode HOME_NORMAL = new DeliveryMode("Home normal", 4);
    private static final DeliveryMode RELAY = new DeliveryMode("Relay", 2);

    @Test
    public void should_add_order_created_event_on_event_store_if_create_order() {
        OrderEventStore eventStore = new OrderEventStore();
        Order order = new Order(eventStore);
        Basket basket = new Basket();

        OrderEvent event = order.create(basket);

        assertThat(event).isInstanceOf(OrderCreatedEvent.class);
    }

    @Test
    public void should_order_has_event_items_if_apply_order_created_event() {
        int orderId = new Random().nextInt();
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_LALOUVIERE, CHATEAU_PIPEAU));

        Order order = new Order(new OrderEventStore());
        order.apply(event);

        assertThat(order.getItems()).containsExactlyInAnyOrder(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE);
    }

    @Test
    public void should_return_delivery_mode_chosen_event_if_choose_delivery_mode() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        Order order = eventStore.getOrder(orderId);

        OrderEvent event = order.chooseDeliveryMode(RELAY);

        assertThat(event).isInstanceOf(DeliveryModeChosenEvent.class);
        assertThat(((DeliveryModeChosenEvent) event).getDeliveryMode()).isEqualTo(RELAY);
    }

    @Test
    public void should_delivery_mode_filled_if_apply_delivery_mode_chosen_event() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        Order order = eventStore.getOrder(orderId);
        DeliveryModeChosenEvent event = new DeliveryModeChosenEvent(orderId, RELAY);

        order.apply(event);

        assertThat(order.getDeliveryMode()).isEqualTo(RELAY);
    }

    @Test
    public void should_throw_exception_if_choose_delivery_mode_on_not_created_order() {
        OrderEventStore eventStore = new OrderEventStore();
        Order order = new Order(eventStore);

        assertThatThrownBy(() -> order.chooseDeliveryMode(RELAY)).isInstanceOf(CannotChooseDeliveryModeOnNotCreatedOrder.class);
    }

    @Test
    public void should_order_not_created_if_choose_delivery_mode_on_not_created_order_when_another_one_is_created() {
        OrderEventStore eventStore = new OrderEventStore();
        int createdOrderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(createdOrderId, new ArrayList<>()));

        int notCreatedOrderId = new Random().nextInt();

        Order notCreatedOrder = eventStore.getOrder(notCreatedOrderId);

        assertThatThrownBy(() -> notCreatedOrder.chooseDeliveryMode(RELAY)).isInstanceOf(CannotChooseDeliveryModeOnNotCreatedOrder.class);
    }

    @Test
    public void should_order_total_price_is_equals_to_basket_items_price_sum_if_order_created() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));

        Order order = eventStore.getOrder(orderId);
        assertThat(order.getAmount()).isEqualTo(50);
    }

    @Test
    public void should_order_total_price_is_equals_to_sum_basket_items_price_sum_and_delivery_price_if_order_delivery_mode_chosen() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, new DeliveryMode("Relay", 2)));
        Order order = eventStore.getOrder(orderId);
        assertThat(order.getAmount()).isEqualTo(52);
    }

    @Test
    public void should_add_paid_order_event_if_paid_an_order_with_delivery_mode_chosen() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, new ArrayList<>()));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        Order order = eventStore.getOrder(orderId);

        order.pay();

        assertThat(eventStore.getEvents()).hasSize(3);
        assertThat(eventStore.getEvents().get(2)).isInstanceOf(PaidEvent.class);
        assertThat(order.isPaid()).isTrue();
    }

    @Test
    public void should_throw_exception_if_paid_an_order_with_no_delivery_mode_chosen() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, new ArrayList<>()));
        Order order = eventStore.getOrder(orderId);

        assertThatThrownBy(order::pay).isInstanceOf(CannotPaidOrder.class);
    }

    @Test
    public void should_return_delivery_mode_change_event_if_choose_delivery_mode_when_delivery_is_set() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        Order order = eventStore.getOrder(orderId);

        OrderEvent event = order.chooseDeliveryMode(RELAY);

        assertThat(event).isInstanceOf(DeliveryModeChanged.class);
        assertThat(((DeliveryModeChanged) event).getDeliveryMode()).isEqualTo(RELAY);
    }

    @Test
    public void should_delivery_mode_changed_and_amount_changed_if_apply_delivery_mode_changed_event() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        Order order = eventStore.getOrder(orderId);
        DeliveryModeChanged event = new DeliveryModeChanged(orderId, HOME_NORMAL);

        order.apply(event);

        assertThat(order.getDeliveryMode()).isEqualTo(HOME_NORMAL);
        assertThat(order.getAmount()).isEqualTo(54);
    }

    @Test
    public void should_item_added_event_and_update_amount_and_update_item_list_if_add_item() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        Order order = eventStore.getOrder(orderId);
        Item tariquet = TARIQUET;

        order.addItem(tariquet);
        assertThat(eventStore.getEvents()).hasSize(3);
        assertThat(eventStore.getEvents().get(2)).isInstanceOf(ItemAdded.class);
        assertThat(order.getItems()).contains(tariquet);
        assertThat(order.getAmount()).isEqualTo(62);
    }

    @Test
    public void should_throw_exception_if_add_item_on_paid_order() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        eventStore.add(new PaidEvent(orderId, 52));
        Order order = eventStore.getOrder(orderId);

        assertThatThrownBy(() -> order.addItem(TARIQUET)).isInstanceOf(CannotAddOrRemoveItemOnPaidOrder.class);
    }

    @Test
    public void should_throw_exception_if_remove_item_on_paid_order() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        eventStore.add(new PaidEvent(orderId, CHATEAU_PIPEAU.getPrice() + CHATEAU_LALOUVIERE.getPrice() + RELAY.getPrice()));
        Order order = eventStore.getOrder(orderId);

        assertThatThrownBy(() -> order.removeItem(CHATEAU_PIPEAU)).isInstanceOf(CannotAddOrRemoveItemOnPaidOrder.class);
    }

    @Test
    public void should_item_removed_event_and_update_amount_and_update_item_list_if_remove_item() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(CHATEAU_PIPEAU, CHATEAU_LALOUVIERE)));
        eventStore.add(new DeliveryModeChosenEvent(orderId, RELAY));
        Order order = eventStore.getOrder(orderId);

        order.removeItem(CHATEAU_PIPEAU);
        assertThat(eventStore.getEvents()).hasSize(3);
        assertThat(eventStore.getEvents().get(2)).isInstanceOf(ItemRemoved.class);
        assertThat(order.getItems()).doesNotContain(CHATEAU_PIPEAU);
        assertThat(order.getAmount()).isEqualTo(CHATEAU_LALOUVIERE.getPrice() + RELAY.getPrice());
    }
}