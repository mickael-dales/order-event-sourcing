package fr.mdales.ordereventsourcing.event;

import fr.mdales.ordereventsourcing.domain.Item;
import fr.mdales.ordereventsourcing.queries.FullOrderDTO;
import fr.mdales.ordereventsourcing.queries.FullOrderRepository;
import fr.mdales.ordereventsourcing.queries.LastFiveOrderRepository;
import fr.mdales.ordereventsourcing.queries.OrderSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventDispatcher {

    private List<EventHandler> eventHandlers = new ArrayList<>();
    private final FullOrderRepository fullOrderRepository;
    private final LastFiveOrderRepository lastFiveOrderRepository;

    public EventDispatcher(FullOrderRepository fullOrderRepository, LastFiveOrderRepository lastFiveOrderRepository) {
        this.fullOrderRepository = fullOrderRepository;
        this.lastFiveOrderRepository = lastFiveOrderRepository;
    }


    public void execute(OrderCreatedEvent event) {
        FullOrderDTO fullOrderDTO = new FullOrderDTO(null, 0, event.getItems().stream().mapToDouble(Item::getPrice).sum(), false);
        fullOrderDTO.getItems().addAll(event.getItems().stream().map(Item::getName).collect(Collectors.toList()));
        fullOrderRepository.store(event.getOrderId(), fullOrderDTO);
        OrderSummaryDTO summaryDTO = new OrderSummaryDTO(event.getItems().stream().map(Item::getName).collect(Collectors.toList()));
        lastFiveOrderRepository.put(event.getOrderId(), summaryDTO);
    }

    public void execute(OrderEvent event) {
    }
}
