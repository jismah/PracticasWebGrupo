package org.jismah;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.jismah.controladores.TemplateHandler;
import org.jismah.servicios.BootStrapServices;
import org.jismah.servicios.CockroachServices;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        BootStrapServices.getInstance().init();

        var app = Javalin.create(javalinConfig -> {
                    javalinConfig.staticFiles.add( staticFileConfig -> {
                        staticFileConfig.hostedPath = "/";
                        staticFileConfig.directory = "/public";
                        staticFileConfig.location = Location.CLASSPATH;
                    });
                });

        app.start(7000);

        System.out.println("* Servidor Arriba!");

        //Manejo de plantillas.
        new TemplateHandler(app).aplicarRutas();

        //Manejadores de Endpoints
        //new HandlerData(app).aplicarRutas();

        //Manejadores de Envio de DATA
        //new Handlers(app).aplicarRutas();

        //Display de contenido en CockroachDB
        try {
            System.out.println("\nCockroachDB - Informacion en tablas: ");
            CockroachServices.getInstance().displayAllSales();
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}