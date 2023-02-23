package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.jismah.entidades.Product;

import java.math.BigDecimal;
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

    public void newProduct(String name, BigDecimal price) {
        create(new Product(name, price));
    }

    public Product getProductById(UUID id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select p from Product p where p.id = :id");
        query.setParameter("id", id);
        try {
            Product product = (Product) query.getSingleResult();
            return product;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Product getProductByName(String name) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select p from Product p where p.name = :name");
        query.setParameter("name", name);
        try {
            Product product = (Product) query.getSingleResult();
            return product;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Product> getAllProducts() {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from Product ", Product.class);
        return query.getResultList();
    }
}
