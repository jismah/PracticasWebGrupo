package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jismah.entidades.Sale;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SaleServices extends GestionBD<Sale> {

    private static SaleServices instance;

    public SaleServices() {
        super(Sale.class);
    }

    public static SaleServices getInstance() {
        if (instance == null) {
            instance = new SaleServices();
        }
        return instance;
    }

    public void newSale(String clientName, Map<UUID, Integer> items) {
        create(new Sale(clientName, items));
    }

    public List<Sale> getSales() {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from Sale", Sale.class);
        return (List<Sale>) query.getResultList();
    }
}
