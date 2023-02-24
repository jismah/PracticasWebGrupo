package org.jismah.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity(name = "Foto")
public class ProductImage implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String mimeType;
    private String image;

    public ProductImage(String mimeType, String image) {
        this.mimeType = mimeType;
        this.image = image;
    }

    public ProductImage() {}

    public String getSource() {
        return "data:" + mimeType + ";base64," + image;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
