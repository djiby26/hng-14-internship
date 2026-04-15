package com.java.hng;

import io.javalin.Javalin;

import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class App 
{
    public static void main( String[] args )
    {
        Javalin app = Javalin.create(config ->
        {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });

            config.routes.apiBuilder(() -> {
                path("/", ()->{
                    get((ctx)->{
                        ctx.json(Map.of("message","API is running"));
                    });
                    path("/health", () -> {
                        get((ctx)->{
                            ctx.json(Map.of("message", "healthy"));
                        });
                    });
                    path("/me", () -> {
                        get((ctx)-> {
                            ctx.json(
                                Map.of(
                                    "name", "Djiby Ndione",
                                    "email","djibyndione26@gmail.com",
                                    "github", "https://github.com/djiby26"
                            ));
                        });
                    });
                });
            });

        }).start(8080);
    }
}
