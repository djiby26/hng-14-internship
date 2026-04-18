package com.hng;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.hng.controller.ProfileController;
import com.hng.dao.ProfileDao;
import com.hng.dto.ApiResponse;
import com.hng.exceptions.ExternalApiException;
import com.hng.service.EnrichmentService;
import com.hng.service.ProfileService;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.Map;

public class App 
{
    public static void main( String[] args )
    {

        ProfileDao        dao        = new ProfileDao();
        EnrichmentService enrichment = new EnrichmentService();
        ProfileService    service    = new ProfileService(dao, enrichment);
        ProfileController controller = new ProfileController(service);

        Javalin.create(config -> {
                    config.jsonMapper(new JavalinJackson().updateMapper( mapper ->{
                        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                    }));

                    config.routes.apiBuilder(controller::registerRoutes);

                    config.routes.exception(IllegalArgumentException.class, (e, ctx) ->
                        ctx.status(400).json(ApiResponse.error(e.getMessage()))
                    );

                    config.routes.exception(ExternalApiException.class, (e, ctx) ->
                        ctx.status(502).json(Map.of(
                                "status",  "502",
                                "message", e.getMessage()   // "{apiName} returned an invalid response"
                        ))
                    );

                    config.routes.exception(Exception.class, (e, ctx) -> {
                        e.printStackTrace();
                        ctx.status(500).json(ApiResponse.error("Internal server error"));
                    });
                }).start(8080);
    }
}
