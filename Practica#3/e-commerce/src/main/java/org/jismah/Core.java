package org.jismah;

import org.jismah.entidades.Cart;
import org.jismah.entidades.Product;
import org.jismah.entidades.Sale;
import org.jismah.entidades.User;
import org.jismah.servicios.ProductServices;
import org.jismah.servicios.SaleServices;
import org.jismah.servicios.UserServices;

import java.math.BigDecimal;
import java.util.*;

public class Core {
    private static Core instance;
    private List<Product> products;
    private List<User> users;
    private Map<String, Cart> carts;
    private List<Sale> sales;

    private Core() {
        this.products = new ArrayList<>();
        this.users = new ArrayList<>();
        this.carts = new HashMap<>();
        this.sales = new ArrayList<>();
    }

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
        }
        return instance;
    }

    // Manejo de Usuarios
    public void addUser(String username, String name, String password) {
        UserServices.getInstance().newUser(username, name, password);
    }

    public void editUser(String old, String username, String name, String password) {
        User user = getUserByUsername(old);
        user.setName(name);
        user.setPassword(password);
        user.setUsername(username);

        UserServices.getInstance().edit(user);
    }

    public List<User> getAllUsers() {
        return UserServices.getInstance().getAllUsers();
    }

    public void deleteUser(User user) {
        UserServices.getInstance().delete(user.getId());
    }

    public User getUserByUsername(String username) {
        return UserServices.getInstance().getUserByUsername(username);
    }

    public boolean authenticateUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            return false;
        }
        if (user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }


    //Manejo de Productos
    public void addProduct(String name, BigDecimal price, String description) {
        ProductServices.getInstance().newProduct(name, price, description);
    }

    public void editProduct(UUID id, String name, BigDecimal price, String description) {
        Product product = getProductById(id);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);

            ProductServices.getInstance().edit(product);
        } else {
            System.out.print("No se encontro el producto con id: " + id.toString());
        }
    }
    public Product getProductById(UUID id) {
        return ProductServices.getInstance().getProductById(id);
    }

    public Product getProductByName(String name) {
        return ProductServices.getInstance().getProductByName(name);
    }

    public void deleteProduct(Product product) {
        ProductServices.getInstance().delete(product.getId());
    }

    public List<Product> getAllProducts() {
        return ProductServices.getInstance().getAllProducts();
    }

    // Manejo de Ventas
    public void newSale(String sessionId, String clientName) {
        Cart cart = getCartForSession(sessionId);
        SaleServices.getInstance().newSale(clientName,cart.getItems());
    }

    public List<Sale> getSales() {
        return SaleServices.getInstance().getSales();
    }

    // Manejo de carrito
    public Cart getCartForSession(String sessionId) {
        if (!carts.containsKey(sessionId)) {
            carts.put(sessionId, new Cart());
        }
        return carts.get(sessionId);
    }

    public void addItemToCart(String sessionId, UUID productId, int cant) {
        Cart cart = getCartForSession(sessionId);
        cart.addItem(productId, cant);
    }

    public void removeItemFromCart(String sessionId, UUID productId) {
        Cart cart = getCartForSession(sessionId);
        cart.removeItem(productId);
    }

    public void reduceItemfromCart(String sessionId, UUID productId, int cant) {
        Cart cart = getCartForSession(sessionId);
        cart.reduceItem(productId, cant);
    }

    public Map<UUID, Integer> getItemsInCart(String sessionId) {
        Cart cart = getCartForSession(sessionId);
        return cart.getItems();
    }

    public List<Product> getProductsInCart(String sessionId) {
        Cart cart = getCartForSession(sessionId);
        List<Product> products = new ArrayList<>();
        for (UUID id : cart.getItems().keySet()) {
            products.add(getProductById(id));
        }
        return products;
    }

    public Integer getCartCountBySession(String sessionId) {
        Cart cart = getCartForSession(sessionId);
        return cart.getItemCount();
    }

    public BigDecimal getCartTotal(String sessionId) {
        Cart cart = getCartForSession(sessionId);
        return cart.getTotal();
    }

    public void clearCartBySession(String sessionId) {
        Cart cart = getCartForSession(sessionId);
        cart.clearCart();
    }

}
