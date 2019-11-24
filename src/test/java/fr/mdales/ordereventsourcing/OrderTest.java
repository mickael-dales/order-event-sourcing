package fr.mdales.ordereventsourcing;

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

    private DeliveryMode relayTwoDeliveryMode = new DeliveryMode("Relay", 2);

    @Test
    public void should_add_order_created_event_on_event_store_if_create_order() {
        OrderEventStore eventStore = new OrderEventStore();
        Order order = new Order(eventStore);
        Basket basket = new Basket();

        order.create(basket);

        assertThat(order.getItems()).containsExactly(basket.getItems().toArray(new Item[basket.getItems().size()]));
        assertThat(eventStore.getEvents()).hasSize(1);
        assertThat(eventStore.getEvents().get(0)).isInstanceOf(OrderCreatedEvent.class);
    }

    @Test
    public void should_add_delivery_mode_chosen_event_on_event_store_if_choose_delivery_mode() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(new Item("Château Pipeau", 20), new Item("Château Lalouvière", 30))));
        Order order = eventStore.getOrder(orderId);

        order.chooseDeliveryMode(relayTwoDeliveryMode);

        assertThat(order.getDeliveryMode()).isEqualTo(relayTwoDeliveryMode);
        assertThat(eventStore.getEvents()).hasSize(2);
        assertThat(eventStore.getEvents().get(1)).isInstanceOf(DeliveryModeChosenEvent.class);
    }

    @Test
    public void should_throw_exception_if_choose_delivery_mode_on_not_created_order() {
        OrderEventStore eventStore = new OrderEventStore();
        Order order = new Order(eventStore);

        assertThatThrownBy(() -> order.chooseDeliveryMode(relayTwoDeliveryMode)).isInstanceOf(CannotChooseDeliveryModeOnNotCreatedOrder.class);
    }

    @Test
    public void should_order_not_created_if_choose_delivery_mode_on_not_created_order_when_another_one_is_created() {
        OrderEventStore eventStore = new OrderEventStore();
        int createdOrderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(createdOrderId, new ArrayList<>()));

        int notCreatedOrderId = new Random().nextInt();

        Order notCreatedOrder = eventStore.getOrder(notCreatedOrderId);

        assertThatThrownBy(() -> notCreatedOrder.chooseDeliveryMode(relayTwoDeliveryMode)).isInstanceOf(CannotChooseDeliveryModeOnNotCreatedOrder.class);
    }

    @Test
    public void should_order_total_price_is_equals_to_basket_items_price_sum_if_order_created() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(new Item("Château Pipeau", 20), new Item("Château Lalouvière", 30))));

        Order order = eventStore.getOrder(orderId);
        assertThat(order.getAmount()).isEqualTo(50);
    }

    @Test
    public void should_order_total_price_is_equals_to_sum_basket_items_price_sum_and_delivery_price_if_order_delivery_mode_chosen() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(orderId, Arrays.asList(new Item("Château Pipeau", 20), new Item("Château Lalouvière", 30))));
        eventStore.add(new DeliveryModeChosenEvent(orderId, new DeliveryMode("Relay", 2)));
        Order order = eventStore.getOrder(orderId);
        assertThat(order.getAmount()).isEqualTo(52);
    }

    @Test
    public void should_add_paid_order_event_if_paid_an_order_with_delivery_mode_chosen() {
        OrderEventStore eventStore = new OrderEventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId, new ArrayList<>()));
        eventStore.add(new DeliveryModeChosenEvent(orderId, relayTwoDeliveryMode));
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


}
