package org.jismah.servicios;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;

import java.lang.reflect.Field;
import java.util.List;

public class GestionBD<T> {

    private static EntityManagerFactory emf;
    private Class<T> classEntity;


    public GestionBD (Class<T> classEntity) {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("persistenceUnit");
        }
        this.classEntity = classEntity;

    }

    public EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    private Object getFieldValue(T entity){
        if (entity == null) {
            return null;
        }

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(entity);
                    return fieldValue;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public T create(T entity) throws IllegalArgumentException, PersistenceException {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return entity;
    }

    public T edit(T entity) throws PersistenceException {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        try {
            em.merge(entity);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return entity;
    }


    public void delete(Object entityId) throws PersistenceException {

        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        try {
            T entity = em.find(classEntity, entityId);
            em.remove(entity);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    public T find(Object id) throws PersistenceException {
        EntityManager em = getEntityManager();

        try {
            return em.find(classEntity, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(classEntity);
            criteriaQuery.select(criteriaQuery.from(classEntity));
            return em.createQuery(criteriaQuery).getResultList();

        } finally {
            em.close();
        }
    }
}
