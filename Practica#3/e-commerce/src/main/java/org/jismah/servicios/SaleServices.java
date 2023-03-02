package org.jismah.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jismah.entidades.Sale;

import java.sql.SQLException;
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
        Sale sale = new Sale(clientName, items);
        create(sale);
        try {
            CockroachServices.getInstance().insertSale(sale);
            System.out.println("\nCockroachDB - Informacion en tablas: ");
            CockroachServices.getInstance().displayAllSales();
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Sale> getSales() {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from Sale", Sale.class);
        return (List<Sale>) query.getResultList();
    }
}
