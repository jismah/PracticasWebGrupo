package org.jismah;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.jismah.controladores.TemplateHandler;

public class Main {



    public static void main(String[] args) {
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
    }
}