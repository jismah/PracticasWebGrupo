package org.jismah.servicios;

import org.jismah.entidades.Sale;
import org.jismah.entidades.SaleItem;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.util.PSQLException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

public class CockroachServices {

    private static CockroachServices instance;

    public static CockroachServices getInstance() {
        if (instance == null) {
            instance = new CockroachServices();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl("jdbc:postgresql://pastel-kakapo-9155.7tt.cockroachlabs.cloud:26257/defaultdb?sslmode=verify-full");
        ds.setUser("sa");
        ds.setPassword("DtzMSVr8xorecP3F5oJpmw");
        ds.setSslmode("require");
        return ds.getConnection();
    }

    public void insertSale(Sale sale) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String saleQuery = "INSERT INTO Sale (dateSale, clientName) VALUES (?, ?)";
            ps = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, new java.sql.Date(sale.getDateSale().getTime()));
            ps.setString(2, sale.getClientName());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            rs.next();
            long saleId = rs.getLong(1);

            String saleItemQuery = "INSERT INTO SaleItem (productId, productName, price, quantity, sale_id) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(saleItemQuery);
            for (SaleItem saleItem : sale.getSales()) {
                ps.setObject(1, saleItem.getProductId());
                ps.setString(2, saleItem.getProductName());
                ps.setBigDecimal(3, saleItem.getPrice());
                ps.setInt(4, saleItem.getQuantity());
                ps.setLong(5, saleId);
                ps.executeUpdate();
            }
        } finally {

            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void displayAllSales() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT s.id, s.dateSale, s.clientName, si.id, si.productId, si.productName, si.price, si.quantity " +
                    "FROM Sale s INNER JOIN SaleItem si ON s.id = si.sale_id ORDER BY s.id LIMIT 10";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            long currentSaleId = 0;
            while (rs.next()) {
                long saleId = rs.getLong(1);
                Date dateSale = rs.getDate(2);
                String clientName = rs.getString(3);
                long saleItemId = rs.getLong(4);
                UUID productId = (UUID) rs.getObject(5);
                String productName = rs.getString(6);
                BigDecimal price = rs.getBigDecimal(7);
                int quantity = rs.getInt(8);

                if (saleId != currentSaleId) {
                    System.out.println("Sale ID: " + saleId);
                    System.out.println("Date: " + dateSale);
                    System.out.println("Client Name: " + clientName);
                    currentSaleId = saleId;
                }

                System.out.println("\tSale Item ID: " + saleItemId);
                System.out.println("\tProduct ID: " + productId);
                System.out.println("\tProduct Name: " + productName);
                System.out.println("\tPrice: " + price);
                System.out.println("\tQuantity: " + quantity);
                System.out.println();
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


    public void StartDB() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = getConnection();

            stmt = conn.createStatement();

            String sql = "CREATE TABLE Sale (" +
                    "    id SERIAL PRIMARY KEY," +
                    "    dateSale DATE," +
                    "    clientName VARCHAR(255)" +
                    ");" +
                    "CREATE TABLE SaleItem (" +
                    "    id SERIAL PRIMARY KEY," +
                    "    productId UUID," +
                    "    productName VARCHAR(255)," +
                    "    price DECIMAL(10,2)," +
                    "    quantity INT," +
                    "    sale_id INT," +
                    "    CONSTRAINT fk_sale_item FOREIGN KEY (sale_id) REFERENCES Sale(id)" +
                    ");";

            stmt.executeUpdate(sql);
            System.out.println("CockroachDB: Se crearon las tablas en la base de datos cockroach");
        } catch (PSQLException se) {
            System.out.println("CockroachDB: Las tablas ya existen en la base de datos cockroach");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}


