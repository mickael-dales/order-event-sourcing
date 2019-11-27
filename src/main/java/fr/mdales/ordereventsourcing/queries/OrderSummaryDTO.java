package fr.mdales.ordereventsourcing.queries;

import java.util.List;

public class OrderSummaryDTO {
    private final List<String> items;

    public OrderSummaryDTO(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }
}
