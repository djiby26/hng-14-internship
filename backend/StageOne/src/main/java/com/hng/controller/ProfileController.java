package com.hng.controller;

import com.hng.dto.ApiResponse;
import com.hng.model.ProfileSummary;
import com.hng.service.ProfileService;
import com.hng.service.ProfileService.CreateResult;
import io.javalin.http.Context;
import static io.javalin.apibuilder.ApiBuilder.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    public void registerRoutes() {
        path("/api/profiles", () -> {
            post(this::create);
            get (this::getAll);
            path("/{id}", () -> {
                get   (this::getById);
                delete(this::deleteP);
            });
        });
    }

    // -------------------------------------------------------------------------
    // POST /api/profiles  — enriches and stores; idempotent on name
    // -------------------------------------------------------------------------

    private void create(Context ctx) throws Exception {
        Map<?, ?> body = ctx.bodyAsClass(Map.class);
        String name = (String) body.get("name");

        if (name == null || name.isBlank()) {
            ctx.status(400).json(ApiResponse.error("Missing or empty name"));
            return;
        }

        CreateResult result = service.createOrGet(name);

        if (result.isNew()) {
            ctx.status(201).json(ApiResponse.success(result.profile()));
        } else {
            ctx.status(200).json(ApiResponse.success("Profile already exists", result.profile()));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/profiles
    // -------------------------------------------------------------------------

    private void getAll(Context ctx) throws SQLException {
        String gender    = ctx.queryParam("gender");
        String countryId = ctx.queryParam("country_id");
        String ageGroup  = ctx.queryParam("age_group");

        List<ProfileSummary> all = service.getAll(gender, countryId, ageGroup);
        ctx.json(ApiResponse.successForAll(all, all.size()));
    }

    // -------------------------------------------------------------------------
    // GET /api/profiles/{id}
    // -------------------------------------------------------------------------

    private void getById(Context ctx) throws SQLException {
        String id = ctx.pathParam("id");

        service.getById(id)
                .ifPresentOrElse(
                        profile -> ctx.json(ApiResponse.success(profile)),
                        ()      -> ctx.status(404).json(ApiResponse.error("Profile not found"))
                );
    }

    // -------------------------------------------------------------------------
    // DELETE /api/profiles/{id}
    // -------------------------------------------------------------------------

    private void deleteP(Context ctx) throws SQLException {
        boolean deleted = service.delete(ctx.pathParam("id"));

        if (deleted) {
            ctx.status(204);
        } else {
            ctx.status(404).json(ApiResponse.error("Profile not found"));
        }
    }
}