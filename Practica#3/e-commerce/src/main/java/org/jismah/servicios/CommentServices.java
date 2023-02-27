package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jismah.entidades.Comment;
import org.jismah.entidades.Product;

import java.util.List;

public class CommentServices extends GestionBD {
    private static CommentServices instance;
    public CommentServices() {
        super(Comment.class);
    }

    public static CommentServices getInstance() {
        if (instance == null) {
            instance = new CommentServices();
        }
        return instance;
    }

    public List<Comment> getCommentsOfProduct(Product product) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select c from Comment c where c.product = :product");
        query.setParameter("product", product);
        return (List<Comment>) query.getResultList();
    }
}
