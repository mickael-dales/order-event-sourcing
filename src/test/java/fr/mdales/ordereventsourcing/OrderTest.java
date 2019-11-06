package fr.mdales.ordereventsourcing;

import fr.mdales.ordereventsourcing.exception.CannotChooseDeliveryModeOnNotCreatedOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JUnit4.class)
public class OrderTest {

    @Test
    public void should_add_order_created_event_on_event_store_if_create_order() {
        EventStore eventStore = new EventStore();
        Order order = new Order(eventStore);

        order.create();

        assertThat(eventStore.getEvents()).hasSize(1);
        assertThat(eventStore.getEvents().get(0)).isInstanceOf(OrderCreatedEvent.class);
    }

    @Test
    public void should_add_delivery_mode_chosen_event_on_event_store_if_choose_delivery_mode() {
        EventStore eventStore = new EventStore();
        int orderId = new Random().nextInt();

        eventStore.add(new OrderCreatedEvent(orderId));
        Order order = new Order(eventStore, orderId);

        order.chooseDeliveryMode();

        assertThat(eventStore.getEvents()).hasSize(2);
        assertThat(eventStore.getEvents().get(1)).isInstanceOf(DeliveryModeChosenEvent.class);
    }

    @Test
    public void should_throw_exception_if_choose_delivery_mode_on_not_created_order() {
        EventStore eventStore = new EventStore();
        Order order = new Order(eventStore);

        assertThatThrownBy(order::chooseDeliveryMode).isInstanceOf(CannotChooseDeliveryModeOnNotCreatedOrder.class);
    }

    @Test
    public void should_order_not_created_if_choose_delivery_mode_on_not_created_order_when_another_one_is_created() {
        EventStore eventStore = new EventStore();
        int createdOrderId = new Random().nextInt();
        eventStore.add(new OrderCreatedEvent(createdOrderId));
        int notCreatedOrderId = new Random().nextInt();

        Order notCreatedOrder = new Order(eventStore, notCreatedOrderId);

        assertThatThrownBy(notCreatedOrder::chooseDeliveryMode).isInstanceOf(CannotChooseDeliveryModeOnNotCreatedOrder.class);
    }
}
