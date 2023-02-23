package org.jismah;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.rendering.JavalinRenderer;
import org.jismah.controladores.HandlerData;
import org.jismah.controladores.Handlers;
import org.jismah.controladores.TemplateHandler;
import org.jismah.entidades.User;
import org.jismah.Core;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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