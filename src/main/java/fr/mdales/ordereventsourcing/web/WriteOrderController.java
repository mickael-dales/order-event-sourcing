package fr.mdales.ordereventsourcing.web;

import fr.mdales.ordereventsourcing.Basket;
import fr.mdales.ordereventsourcing.DeliveryMode;
import fr.mdales.ordereventsourcing.Order;
import fr.mdales.ordereventsourcing.OrderEventStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class WriteOrderController {

    private final OrderEventStore orderEventStore;

    public WriteOrderController(OrderEventStore orderEventStore) {
        this.orderEventStore = orderEventStore;
    }

    @PostMapping
    public ResponseEntity<Integer> create(Basket basket) {
        Order order = new Order(orderEventStore);
        order.create(basket);
        return ResponseEntity.ok(order.getId());
    }

    @PostMapping("{orderId}/delivery-mode")
    public ResponseEntity chooseDeliveryMode(@PathVariable Integer orderId, String name, double price) {
        Order order = new Order(orderEventStore, orderId);
        order.chooseDeliveryMode(new DeliveryMode(name, price));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{orderId}/pay")
    public ResponseEntity pay(@PathVariable Integer orderId) {
        Order order = new Order(orderEventStore, orderId);
        order.pay();
        return ResponseEntity.noContent().build();
    }

}
