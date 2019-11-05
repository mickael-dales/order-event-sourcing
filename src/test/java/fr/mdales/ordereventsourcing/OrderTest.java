package fr.mdales.ordereventsourcing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class OrderTest {

    @Test
    public void should_add_event_on_event_store_if_create_order() {
        EventStore eventStore = new EventStore();
        Order order = new Order(eventStore);

        order.create();

        assertThat(eventStore.getEvents()).hasSize(1);
    }


}
