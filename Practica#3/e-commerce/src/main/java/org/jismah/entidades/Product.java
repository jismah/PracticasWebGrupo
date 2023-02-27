package org.jismah.entidades;


import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Product implements Serializable {
    @Id
    private UUID id;
    private String name;
    private BigDecimal price;
    private String description;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_image_fk")
    private List<ProductImage> images;

    public Product() {}

    public Product(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.id = UUID.randomUUID();
        this.description = description;
        this.images = new ArrayList<>();
    }

    public void addImage(ProductImage image) {
        images.add(image);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public void removeImages() {
        images.clear();
    }
}
