package fr.mdales.ordereventsourcing.queries;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FullOrderRepository {
    Map<Integer, FullOrderDTO> store = new HashMap<>();

    public Optional<FullOrderDTO> get(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public void store(int id, FullOrderDTO fullOrderDTO) {
        if (get(id).isPresent()) {
            store.replace(id, fullOrderDTO);
        } else {
            store.put(id, fullOrderDTO);
        }
    }
}
