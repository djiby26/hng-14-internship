package com.java.hng;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.java.hng.exceptions.InvalidNameException;
import com.java.hng.exceptions.MissingNameException;
import io.javalin.Javalin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

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
            config.routes.get("/api/classify", ctx ->
            {
                String name = ctx.queryParam("name");
                if (name == null || name.isEmpty()){
                    throw new MissingNameException("400 Bad Request");
                }
                if (!name.matches("[a-zA-Z]+"))
                {
                    throw new InvalidNameException("422 Unprocessable Entity");
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(
                        URI.create(String.format("https://api.genderize.io?name=%s", name))).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                int sampleSize = json.get("count").getAsInt();
                // Here im checking if there is a result before attempting to access value to avoid null value exceptions
                if (sampleSize != 0){
                    String gender = json.get("gender").getAsString();
                    double probability = json.get("probability").getAsDouble();
                    String processedAt = Instant.now().toString();
                    boolean isConfident = probability >= 0.7 && sampleSize >= 100;

                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("name", name);
                    data.put("gender", gender);
                    data.put("probability", probability);
                    data.put("sample_size", sampleSize);
                    data.put("is_confident", isConfident);
                    data.put("processed_at", processedAt);

                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("status", "success");
                    result.put("data", data);

                    ctx.json(result);
                }else{
                    ctx.json(Map.of("status","error",
                            "message", "No prediction available for the provided name"))
                            .status(400);
                }
            });

        config.routes.exception(MissingNameException.class,(e, ctx) -> {
            ctx.json(Map.of("status","error", "message", e.getMessage())).status(400);
        });

        config.routes.exception(InvalidNameException.class,(e, ctx) -> {
            ctx.json(Map.of("status","error", "message", e.getMessage())).status(422);
        });

        }).start(7070);
    }
}
