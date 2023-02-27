package org.jismah.entidades;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
public class Comment implements Serializable {
    @Id
    private String id;
    @ManyToOne(optional = false)
    private User user;
    @ManyToOne
    private Product product;
    private String comment;

    public Comment(User user, String comment, Product product) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.comment = comment;
        this.product = product;
    }

    public Comment() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
