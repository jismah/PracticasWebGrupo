package org.jismah.util;
import io.javalin.Javalin;

public abstract class BaseHandler {
    protected Javalin app;
    public BaseHandler(Javalin app){
        this.app = app;
    }
    abstract public void aplicarRutas();
}
