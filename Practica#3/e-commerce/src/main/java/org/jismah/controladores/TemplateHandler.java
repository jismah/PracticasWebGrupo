package org.jismah.controladores;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.jismah.Core;
import org.jismah.entidades.Product;
import org.jismah.entidades.ProductImage;
import org.jismah.entidades.Session;
import org.jismah.entidades.User;
import org.jismah.servicios.CommentServices;
import org.jismah.servicios.ProductServices;
import org.jismah.servicios.SessionServices;
import org.jismah.util.BaseHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class TemplateHandler extends BaseHandler {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String SESSION_ID_KEY = "session-id";
    private static final String REMEMBER_ID_KEY = "rememberMe";
    private static User loggedUser = null;

    public TemplateHandler(Javalin app) {
        super(app);
        registrandoPlantillas();
    }

    private void registrandoPlantillas(){
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    public void aplicarRutas() {
        app.routes(() -> {

            app.get("/remember", ctx -> {
                String rememberMe = ctx.cookie(REMEMBER_ID_KEY);
                if (rememberMe != null) {
                    ctx.result("Cookie de remember es: " + rememberMe +"\nSession-id: " + ctx.cookie(SESSION_ID_KEY));
                } else {
                    ctx.result("No se tiene cookie de remember");
                }
            });

            app.get("/sessions", ctx -> {
                List<Session> sessions = SessionServices.getInstance().findAll();
                String res = "";
                for (Session session : sessions) {
                    res += "\n\nID: " + session.getSessionId() + "\nUsername: " + session.getUsername();
                }
                ctx.result(res);
            });
            
            app.get("/", ctx -> {
                String sessionId = getSessionId(ctx);

                System.out.println("isAdmin: " + isAdmin(sessionId));
                if (isAdmin(sessionId)) {
                    Map<String, Object> model = getViewModel(sessionId);
                    model.put("message", "Hola Admin, " + sessionId);
                    ctx.render("/views/messages.html", model);

                } else if (isLoggedInSession(sessionId) != null) {
                    Map<String, Object> model = getViewModel(sessionId);
                    model.put("message", "Hola " + loggedUser.getName() + ",\nSession Id:" + sessionId);
                    ctx.render("/views/messages.html", model);
                } else {
                    ctx.redirect("/products");
                }
            });

            // Logins y registro de usuarios nuevos
            app.get("/login", ctx -> {

                String cookieString = ctx.cookie(REMEMBER_ID_KEY);
                if (cookieString != null) {

                    Session session = SessionServices.getInstance().find(cookieString);
                    if (session != null) {
                        if (session.getUsername().equals(ADMIN_USERNAME)) {
                            loggedUser = Core.getInstance().getUserByUsername(session.getUsername());
                        }
                        ctx.cookie(SESSION_ID_KEY, cookieString);
                        ctx.redirect("/");
                        return;
                    }
                }

                String sessionId = getSessionId(ctx);
                Map<String, Object> model = getViewModel(sessionId);
                ctx.render("/views/login.html", model);
            });

            app.post("/login", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    String username = ctx.formParam("username");
                    String password = ctx.formParam("password");
                    boolean remember = Boolean.parseBoolean(ctx.formParam("remember"));

                    User user;
                    if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                        String sessionId = getSessionId(ctx);
                        SessionServices.getInstance().create(new Session(sessionId, ADMIN_USERNAME));
                        if (remember) {
                            ctx.cookie(REMEMBER_ID_KEY, sessionId, (int) Duration.ofDays(7).getSeconds());
                        }
                    } else {
                        user = Core.getInstance().authenticateUser(username, password);
                        if (user != null) {
                            String sessionId = getSessionId(ctx);
                            SessionServices.getInstance().create(new Session(sessionId, username));
                            loggedUser = user;
                            if (remember) {
                                ctx.cookie(REMEMBER_ID_KEY, sessionId, (int) Duration.ofDays(7).getSeconds());
                            }
                        } else {
                            ctx.status(401);
                            Map<String, Object> model = getViewModel(getSessionId(ctx));
                            model.put("message", "Nombre o clave incorrecta");
                            model.put("return", "/login");
                            ctx.render("/views/messages.html", model);
                            return;
                        }
                    }

                    String sessionId = getSessionId(ctx);
                    ctx.cookie(SESSION_ID_KEY, sessionId);
                    ctx.redirect("/");
                }
            });

            app.get("/logout", ctx -> {
                String sessionId = getSessionId(ctx);

                SessionServices.getInstance().delete(sessionId);

                ctx.removeCookie(SESSION_ID_KEY);
                ctx.removeCookie(REMEMBER_ID_KEY);
                loggedUser = null;
                ctx.redirect("/login");
            });

            //CRUD Usuarios
            Handler userListHandler = ctx -> {
                if (isAdmin(getSessionId(ctx))) {
                    String sessionId = getSessionId(ctx);

                    Map<String, Object> model = getViewModel(sessionId);
                    model.put("users", Core.getInstance().getAllUsers());
                    ctx.render("/views/users.html", model);
                } else {
                    ctx.redirect("/");
                }
            };
            app.get("/users", userListHandler);

            app.get("/user/new", ctx -> {
                String sessionId = getSessionId(ctx);
                Map<String, Object> model = getViewModel(sessionId);
                ctx.render("/views/userDetails.html", model);
            });

            app.post("/user/new", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    String username = ctx.formParam("username");
                    String name = ctx.formParam("name");
                    String password = ctx.formParam("password");

                    if (Core.getInstance().getUserByUsername(username) != null) {
                        ctx.status(401);
                        String sessionId = getSessionId(ctx);

                        Map<String, Object> model = getViewModel(sessionId);
                        model.put("message", "ERROR: Usuario ya existe");
                        model.put("return", "/user/new");
                        ctx.render("/views/messages.html", model);
                    } else {
                        Core.getInstance().addUser(username, name, password);
                        ctx.redirect("/users");
                    }
                }
            });

            app.get("/user/edit", ctx -> {
                String sessionId = getSessionId(ctx);
                if (isAdmin(sessionId)) {
                    String username = ctx.queryParam("username");

                    Map<String, Object> model = getViewModel(sessionId);
                    User user = Core.getInstance().getUserByUsername(username);
                    if (user != null) {
                        model.put("user", user);
                        ctx.render("/views/userDetails.html", model);
                    } else {
                        model.put("message", "Usuario no encontrado");
                        model.put("return", "/products");
                        ctx.render("/views/messages.html", model);
                    }
                } else {
                    ctx.redirect("/");
                }
            });

            app.post("/user/edit", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    String old = ctx.formParam("old");
                    String username = ctx.formParam("username");
                    String name = ctx.formParam("name");
                    String password = ctx.formParam("password");

                    if (Core.getInstance().getUserByUsername(username) != null && !(old.equals(username))) {
                        String sessionId = getSessionId(ctx);

                        Map<String, Object> model = getViewModel(sessionId);
                        model.put("message", "El username debe ser unico");
                        model.put("return", "/users");
                        ctx.render("/views/messages.html", model);
                    } else {
                        Core.getInstance().editUser(old, username, name, password);
                        ctx.redirect("/users");
                    }
                }
            });

            app.post("/user/delete", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    String username = ctx.formParam("username");
                    Core.getInstance().deleteUser(Core.getInstance().getUserByUsername(username));
                    ctx.redirect("/users");
                }
            });

            //Carrito de Compras
            app.get("/cart", ctx -> {
                String sessionId = getSessionId(ctx);

                Map<String, Object> model = getViewModel(sessionId);
                model.put("products", Core.getInstance().getProductsInCart(sessionId));
                model.put("items", Core.getInstance().getItemsInCart(sessionId));
                model.put("total", Core.getInstance().getCartTotal(sessionId));
                ctx.render("/views/cart.html", model);
            });

            app.post("/add-to-cart", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    UUID id = UUID.fromString(ctx.formParam("id"));
                    Integer cant = Integer.valueOf(ctx.formParam("cant"));

                    String sessionId = getSessionId(ctx);
                    Core.getInstance().addItemToCart(sessionId, id, cant);

                    ctx.redirect("/cart");
                }
            });

            app.post("/reduce-from-cart", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    UUID id = UUID.fromString(ctx.formParam("id"));
                    Integer cant = Integer.valueOf(ctx.formParam("cant"));

                    String sessionId = getSessionId(ctx);
                    Core.getInstance().reduceItemfromCart(sessionId, id, cant);

                    ctx.redirect("/cart");
                }
            });

            app.post("/remove-from-cart", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    UUID id = UUID.fromString(ctx.formParam("id"));

                    String sessionId = getSessionId(ctx);
                    Core.getInstance().removeItemFromCart(sessionId, id);

                    ctx.redirect("/cart");
                }
            });

            // CRUD de Productos
            Handler productHandler = ctx -> {
                String sessionId = getSessionId(ctx);
                UUID id = UUID.fromString(ctx.queryParam("id"));

                Map<String, Object> model = getViewModel(sessionId);
                Product product = Core.getInstance().getProductById(id);

                if (product != null) {
                    model.put("product", product);
                    model.put("comments", CommentServices.getInstance().getCommentsOfProduct(product));
                    ctx.render("/views/product.html", model);
                } else {
                    model.put("message", "Producto no encontrado");
                    model.put("return", "/products");
                    ctx.render("/views/messages.html", model);
                }
            };
            app.get("/product", productHandler);

            Handler productListHandler = ctx -> {
                String sessionId = getSessionId(ctx);

                String pageParam = ctx.queryParam("page");
                int currentPage;

                if (pageParam == null || pageParam.isEmpty()) {
                    currentPage = 1;
                } else {
                    currentPage = Integer.parseInt(pageParam);
                }

                Map<String, Object> model = getViewModel(sessionId);
                model.put("products", ProductServices.getInstance().getProductsByPage(currentPage));

                int pageNum = (int) Math.ceil((double) ProductServices.getInstance().getProductCount() / 10);

                if (pageNum <= 1) {
                    pageNum = 1;
                }
                model.put("page_cant", pageNum);
                model.put("current_page", currentPage);
                model.put("next_page", currentPage + 1);
                ctx.render("/views/products.html", model);
            };
            app.get("/products", productListHandler);

            app.get("/product/new", ctx -> {
                String sessionId = getSessionId(ctx);
                if (isAdmin(sessionId)) {
                    Map<String, Object> model = getViewModel(sessionId);
                    ctx.render("/views/productDetails.html", model);
                } else {
                    ctx.redirect("/");
                }
            });

            app.post("/product/new", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    String name = ctx.formParam("name");
                    BigDecimal price = new BigDecimal(ctx.formParam("price"));
                    String description = ctx.formParam("description");

                    if (Core.getInstance().getProductByName(name) != null) {
                        ctx.status(401);
                        String sessionId = getSessionId(ctx);

                        Map<String, Object> model = getViewModel(sessionId);
                        model.put("message", "ERROR: Un producto con ese nombre ya existe");
                        model.put("return", "/product/new");
                        ctx.render("/views/messages.html", model);
                    } else {
                        List<ProductImage> images = new ArrayList<>();
                        ctx.uploadedFiles("images").forEach(uploadedFile -> {
                            try {
                                byte[] bytes = uploadedFile.content().readAllBytes();
                                String encodedString = Base64.getEncoder().encodeToString(bytes);
                                images.add(ProductServices.getInstance().newImage(uploadedFile.contentType(), encodedString));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        ProductServices.getInstance().newProduct(name, price, description, images);
                        ctx.redirect("/products");
                    }
                }
            });

            app.get("/product/edit", ctx -> {
                String sessionId = getSessionId(ctx);
                if (isAdmin(sessionId)) {
                    UUID id = UUID.fromString(ctx.queryParam("id"));

                    Map<String, Object> model = getViewModel(sessionId);
                    Product product = Core.getInstance().getProductById(id);
                    if (product != null) {
                        model.put("product", product);
                        ctx.render("/views/productDetails.html", model);
                    } else {
                        model.put("message", "Producto no encontrado");
                        model.put("return", "/products");
                        ctx.render("/views/messages.html", model);
                    }
                } else {
                    ctx.redirect("/");
                }
            });

            app.post("/product/edit", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    UUID id = UUID.fromString(ctx.formParam("id"));
                    String name = ctx.formParam("name");
                    BigDecimal price = new BigDecimal(ctx.formParam("price"));
                    String description = ctx.formParam("description");

                    if (ctx.uploadedFiles("images").size() > 0) {
                        ProductServices.getInstance().removeImagesFromProduct(id);
                        ctx.uploadedFiles("images").forEach(uploadedFile -> {
                            try {
                                byte[] bytes = uploadedFile.content().readAllBytes();
                                String encodedString = Base64.getEncoder().encodeToString(bytes);
                                ProductServices.getInstance().addImageToProduct(id, uploadedFile.contentType(), encodedString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    Core.getInstance().editProduct(id, name, price, description);
                    ctx.redirect("/products");
                }
            });

            app.post("/product/delete", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    UUID id = UUID.fromString(ctx.formParam("id"));

                    Core.getInstance().deleteProduct(Core.getInstance().getProductById(id));
                    ctx.redirect("/products");
                }
            });

            // Comentarios

            app.post("/comment/new", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    if (loggedUser != null) {
                        UUID productId = UUID.fromString(ctx.formParam("productId"));
                        String comment = ctx.formParam("comment");

                        Core.getInstance().addComment(productId, comment, loggedUser);
                        ctx.redirect("/product?id=" + productId);
                    } else {
                        String sessionId = getSessionId(ctx);
                        Map<String, Object> model = getViewModel(sessionId);
                        model.put("message", "Debe acceder a una cuenta para publicar un comentario");
                        model.put("return", "/products");
                        ctx.render("/views/messages.html", model);
                    }
                }
            });

            app.post("/comment/delete", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    UUID productId = UUID.fromString(ctx.formParam("productId"));
                    String commentId = ctx.formParam("commentId");

                    Core.getInstance().deleteComment(productId, commentId);
                    ctx.redirect("/product?id=" + productId);
                }
            });

            // Ventas

            Handler salesListHandler = ctx -> {
                String sessionId = getSessionId(ctx);

                if (isAdmin(sessionId)) {
                    Map<String, Object> model = getViewModel(sessionId);
                    model.put("sales", Core.getInstance().getSales());

                    ctx.render("/views/sales.html", model);
                } else {
                    Map<String, Object> model = getViewModel(sessionId);
                    model.put("message", "Debe ser administrador para acceder a esta zona");
                    model.put("return", "/");
                    ctx.render("/views/messages.html", model);
                }
            };
            app.get("/sales", salesListHandler);
            app.post("/process-sale", new Handler() {
                @Override
                public void handle(Context ctx) throws Exception {
                    String sessionId = getSessionId(ctx);
                    if (Core.getInstance().getCartCountBySession(sessionId) > 0) {
                        if (isAdmin(sessionId)) {
                            Core.getInstance().newSale(sessionId, "Admin");
                            Core.getInstance().clearCartBySession(sessionId);
                            ctx.redirect("/products");

                        } else if (loggedUser == null) {
                            ctx.status(401);

                            Map<String, Object> model = getViewModel(sessionId);
                            model.put("message", "Necesita entrar a una cuenta antes de comprar");
                            model.put("return", "/login");
                            ctx.render("/views/messages.html", model);

                        } else {
                            Core.getInstance().newSale(sessionId, loggedUser.getName());
                            Core.getInstance().clearCartBySession(sessionId);
                            ctx.redirect("/products");
                        }
                    } else {
                        Map<String, Object> model = getViewModel(sessionId);
                        model.put("message", "Debe tener productos en el carrito para procesar la venta");
                        model.put("return", "/products");
                        ctx.render("/views/messages.html", model);
                    }
                }
            });
        });
    }


    private static User isLoggedInSession(String sessionId) {
        Session session = SessionServices.getInstance().find(sessionId);
        if (session == null) {
            return null;
        }
        loggedUser = Core.getInstance().getUserByUsername(session.getUsername());
        return loggedUser;
    }

    private static String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }

    private static String getSessionId(Context ctx) {
        String sessionId = ctx.cookie(SESSION_ID_KEY);
        if (sessionId == null) {
            sessionId = generateSessionId();
            ctx.cookie(SESSION_ID_KEY, sessionId);
        }
        return sessionId;
    }

    private static boolean isAdmin(String sessionId) {
        Session session = SessionServices.getInstance().find(sessionId);
        if (session == null) {
            System.out.println("Session is null");
            return false;
        }
        System.out.println("Session exists");
        return session.getUsername().equals(ADMIN_USERNAME);
    }

    private static Map<String, Object> getViewModel(String sessionId) {
        Map<String, Object> model = new HashMap<>();
        model.put("isAdmin", isAdmin(sessionId));
        model.put("isLogged", loggedUser != null);
        model.put("itemCount", Core.getInstance().getCartCountBySession(sessionId));
        return model;
    }

}
