package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.jismah.entidades.User;

import java.util.List;

public class UserServices extends GestionBD<User> {
    private static UserServices instance;

    public UserServices() {
        super(User.class);
    }

    public static UserServices getInstance() {
        if (instance == null) {
            instance = new UserServices();
        }
        return instance;
    }

    public List<User> getAllUsers() {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from User ", User.class);
        return query.getResultList();
    }

    public void newUser(String username, String name, String password) {
        create(new User(username, name, password));
    }

    public User getUserByUsername(String username) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select user from User user where user.username = :username");
        query.setParameter("username", username);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }

}
