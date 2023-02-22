package org.example.Encapsulations;

import org.example.Services.Controller;
import org.thymeleaf.context.IContext;

import java.math.BigDecimal;
import java.util.*;

public class Sale {
    private Date dateSale;
    private String clientName;
    private Map<UUID, Integer> sales;

    public Sale(String clientName) {
        this.dateSale = new Date();
        this.clientName = clientName;
        this.sales = new HashMap<>();
    }

    public Sale(String clientName, Map<UUID, Integer> items) {
        this.dateSale = new Date();
        this.clientName = clientName;
        this.sales = new HashMap<>();

        for (Map.Entry<UUID, Integer> entry : items.entrySet()) {
            addItem(entry.getKey(), entry.getValue());
        }
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (UUID id : sales.keySet()) {
            Product p = Controller.getInstance().getProductById(id);
            if (p != null) {
                products.add(p);
            }
        }
        return products;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.valueOf(0.00);
        Product product;
        for (Map.Entry<UUID,Integer> item: sales.entrySet()) {
            product = Controller.getInstance().getProductById(item.getKey());
            if (product != null) {
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getValue())));
            }
        }
        return total;
    }

    public void addItem(UUID id, int cant) {
        sales.put(id, cant);
    }

    public Date getDateSale() {
        return dateSale;
    }

    public String getClientName() {
        return clientName;
    }

    public Map<UUID, Integer> getSales() {
        return sales;
    }
}
