package fr.mdales.ordereventsourcing.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class ReadOrderController {

    @GetMapping("/{id}")
    public ResponseEntity<FullOrderDTO> getOrder(@PathVariable int id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<OrderSummaryDTO> lastFiveOrders() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/paid-count")
    public ResponseEntity<Integer> count(){
        return ResponseEntity.ok(0);
    }
}
