package org.jismah.entidades;

import jakarta.persistence.*;
import org.jismah.Core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Entity
public class Sale implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateSale;
    private String clientName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_item_fk")
    private List<SaleItem> sales;

    public Sale() {}

    public Sale(String clientName, Map<UUID, Integer> items) {
        this.dateSale = new Date();
        this.clientName = clientName;
        this.sales = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : items.entrySet()) {
            UUID productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = Core.getInstance().getProductById(productId);
            if (product != null) {
                SaleItem item = new SaleItem(product.getId(), product.getName(), product.getPrice(), quantity);
                addItem(item);
            }
        }
    }

    public List<SaleItem> getSales() {
        return sales;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : sales) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public void addItem(SaleItem item) {
        sales.add(item);
    }

    public Date getDateSale() {
        return dateSale;
    }

    public String getClientName() {
        return clientName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
