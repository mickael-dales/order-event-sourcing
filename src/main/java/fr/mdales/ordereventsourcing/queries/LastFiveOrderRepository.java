package fr.mdales.ordereventsourcing.queries;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LastFiveOrderRepository {

    private final Map<Integer, OrderSummaryDTO> map = new HashMap<>();

    public List<OrderSummaryDTO> get() {
        return new ArrayList<>(map.values());
    }

    public void put(Integer id, OrderSummaryDTO order) {
        if (map.size() >= 5) {
          map.keySet().stream().findFirst().ifPresent(map::remove);
        }
        map.put(id, order);
    }
}
