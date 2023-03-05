package org.jismah.servicios;

import org.h2.tools.Server;
import org.jismah.Core;
import org.jismah.entidades.ProductImage;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BootStrapServices {
    private static BootStrapServices instance;

    private BootStrapServices() {}

    public static BootStrapServices getInstance(){
        if (instance == null) {
            instance = new BootStrapServices();
        }
        return instance;
    }

    public void startDb() {
        try {
            //Modo servidor H2.
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            //Abriendo el cliente web. El valor 0 representa puerto aleatorio.
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();

            System.out.println("Status Web: "+status);

        } catch (SQLException ex) {
            System.out.println("Problema con la base de datos: "+ex.getMessage());
        }
    }

    public void init() {
        startDb();
        CockroachServices.getInstance().StartDB();

        // Creando 30 productos por default
        int productCount = Core.getInstance().getAllProducts().size();
        if (productCount < 30) {
            List<ProductImage> images = new ArrayList<>();
            String currentPath = new File("").getAbsolutePath();
            String imagePath = currentPath + "/src/main/resources/public/image.jpg";

            for (int i = 0; i < 30-productCount; i++) {
                String encodedString = null;
                try {
                    byte[] bytes = Files.readAllBytes(Paths.get(imagePath));
                    encodedString = Base64.getEncoder().encodeToString(bytes);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                images.add(ProductServices.getInstance().newImage("image/jpeg", encodedString));
            }

            for (int i = 0; i < 30-productCount; i++) {
                List<ProductImage> productImages = new ArrayList<>();
                productImages.add(images.get(i));
                ProductServices.getInstance().newProduct("Producto #" + (productCount+i+1), new BigDecimal(9.99),
                        "Descripcion del producto", productImages);
            }
        }
    }
}
