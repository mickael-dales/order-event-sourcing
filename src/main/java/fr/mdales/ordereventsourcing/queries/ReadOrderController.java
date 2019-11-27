package fr.mdales.ordereventsourcing.queries;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("order")
public class ReadOrderController {

    private final FullOrderRepository fullOrderRepository;
    private final LastFiveOrderRepository lastFiveOrderRepository;

    public ReadOrderController(FullOrderRepository fullOrderRepository, LastFiveOrderRepository lastFiveOrderRepository) {
        this.fullOrderRepository = fullOrderRepository;
        this.lastFiveOrderRepository = lastFiveOrderRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullOrderDTO> getOrder(@PathVariable int id) {
        Optional<FullOrderDTO> fullOrderDTO = fullOrderRepository.get(id);
        return fullOrderDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<OrderSummaryDTO>> lastFiveOrders() {
        return ResponseEntity.ok(lastFiveOrderRepository.get());
    }

    @GetMapping("/paid-count")
    public ResponseEntity<Integer> count() {
        return ResponseEntity.ok(0);
    }
}
