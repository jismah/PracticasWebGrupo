package org.jismah.entidades;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Product implements Serializable {
    @Id
    private UUID id;
    private String name;
    private BigDecimal price;

    public Product() {

    }
    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
