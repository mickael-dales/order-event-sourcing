package fr.mdales.ordereventsourcing.command;

import fr.mdales.ordereventsourcing.domain.Basket;
import fr.mdales.ordereventsourcing.domain.DeliveryMode;
import fr.mdales.ordereventsourcing.domain.Order;
import fr.mdales.ordereventsourcing.event.OrderEventStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("order")
public class WriteOrderController {

    private final CommandHandler commandHandler;
    private final OrderEventStore orderEventStore;

    public WriteOrderController(CommandHandler commandHandler, OrderEventStore orderEventStore) {
        this.commandHandler = commandHandler;
        this.orderEventStore = orderEventStore;
    }

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody Basket basket) {
        Integer id = commandHandler.handle(new CreateOrderCommand(basket.getItems().stream().map(i -> new Item(i.getName(), i.getPrice())).collect(Collectors.toList())));
        return ResponseEntity.ok(id);
    }

    @PostMapping("{orderId}/delivery-mode")
    public ResponseEntity chooseDeliveryMode(@PathVariable Integer orderId, String name, double price) {
        commandHandler.handle(new ChooseDeliveryModeCommand(orderId, name, price));
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
