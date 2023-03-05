package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.jismah.entidades.Session;

public class SessionServices extends GestionBD<Session> {
    private static SessionServices instance;

    public SessionServices() {
        super(Session.class);
    }

    public static SessionServices getInstance() {
        if (instance == null) {
            instance = new SessionServices();
        }
        return instance;
    }

    @Override
    public Session find(String id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select s from Session s where s.sessionId = :id");
        query.setParameter("id", id);
        try {
            return (Session) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
