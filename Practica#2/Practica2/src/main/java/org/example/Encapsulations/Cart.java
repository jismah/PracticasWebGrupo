package org.example.Encapsulations;

import org.example.Services.Controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cart {
    private Map<UUID, Integer> items;

    public Cart() {
        this.items = new HashMap<>();
    }

    public void addItem(UUID id, int cant) {
        int currentCant = items.getOrDefault(id, 0);
        items.put(id, currentCant + cant);
    }

    public void reduceItem(UUID id, int cant) {
        int currentCant = items.getOrDefault(id, 0);
        int newCant = currentCant - cant;
        if (newCant <= 0) {
            items.remove(id);
        } else {
            items.put(id, newCant);
        }
    }

    public void removeItem(UUID id) {
        items.remove(id);
    }

    public Map<UUID, Integer> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.valueOf(0.00);
        Product product;
        for (Map.Entry<UUID,Integer> item: items.entrySet()) {
            product = Controller.getInstance().getProductById(item.getKey());
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getValue())));
        }
        return total;
    }

    public Integer getItemCount() {
        Integer total = 0;
        for (Integer count : items.values()) {
            total += count;
        }
        return  total;
    }

    public void clearCart() {
        items.clear();
    }
}
