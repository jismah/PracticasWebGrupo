package org.example.Services;

import org.example.Encapsulations.Cart;
import org.example.Encapsulations.Product;
import org.example.Encapsulations.Sale;
import org.example.Encapsulations.User;

import java.math.BigDecimal;
import java.util.*;

public class Controller {
    private static Controller instance;
    private List<Product> products;
    private List<User> users;
    private Map<String, Cart> carts;

    private List<Sale> sales;

    private Controller() {
        this.products = new ArrayList<>();
        this.users = new ArrayList<>();
        this.carts = new HashMap<>();
        this.sales = new ArrayList<>();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public Product addProduct(String name, BigDecimal price) {
        Product product = new Product(name, price);
        products.add(product);
        return product;
    }

    public void editProduct(UUID id, String name, BigDecimal price) {
        Product product = getProductById(id);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
        } else {
            System.out.print("No se encontro el producto con id: " + id.toString());
        }
    }

    public User addUser(String username, String name, String password) {
        User user = new User(username, name, password);
        users.add(user);
        return user;
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public Product getProductById(UUID id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public Product getProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    public void deleteProduct(Product product) {
        products.remove(product);
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
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

    public List<Product> getAllProducts() {
        return products;
    }

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

    public void newSale(String sessionId, String clientName) {
        Cart cart = getCartForSession(sessionId);
        sales.add(new Sale(clientName, cart.getItems()));
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void editUser(String old, String username, String name, String password) {
        User user = getUserByUsername(old);
        user.setName(name);
        user.setPassword(password);
        user.setUsername(username);
    }

    public List<User> getAllUsers() {
        return users;
    }
}
