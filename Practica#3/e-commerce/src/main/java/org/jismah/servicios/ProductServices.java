package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.jismah.entidades.Product;
import org.jismah.entidades.ProductImage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductServices extends GestionBD<Product> {
    private static ProductServices instance;

    public ProductServices() {
        super(Product.class);
    }

    public static ProductServices getInstance() {
        if (instance == null) {
            instance = new ProductServices();
        }
        return  instance;
    }

    public Product newProduct(String name, BigDecimal price, String description, List<ProductImage> images) {
        Product product = new Product(name, price, description, images);
        create(product);
        return product;
    }

    public Product getProductById(UUID id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select p from Product p where p.id = :id");
        query.setParameter("id", id);
        try {
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Product getProductByName(String name) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select p from Product p where p.name = :name");
        query.setParameter("name", name);
        try {
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Product> getAllProducts() {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from Product ", Product.class);
        return query.getResultList();
    }

    public void addImageToProduct(UUID id, String mimeType, String image) {
        Product product = getProductById(id);
        product.addImage(new ProductImage(mimeType, image));
    }

    public ProductImage newImage(String mimeType, String image) {
        return new ProductImage(mimeType, image);
    }

    public void removeImagesFromProduct(UUID id) {
        Product product = getProductById(id);
        product.removeImages();
    }

    public Long getProductCount() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select count(*) from Product");
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }

    public List<Product> getProductsByPage(int pageNumber) {
        int startIndex = (pageNumber - 1) * 10;
        int maxResults = 10;
        long productCount = getProductCount();

        if (startIndex >= productCount) {
            return new ArrayList<>();
        }

        if (startIndex + maxResults > productCount) {
            maxResults = (int) (productCount - startIndex);
        }

        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from Product LIMIT :maxResults OFFSET :startIndex", Product.class);
        query.setParameter("maxResults", maxResults);
        query.setParameter("startIndex", startIndex);

        return query.getResultList();
    }

}
