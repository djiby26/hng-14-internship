package com.hng.controller;

import com.hng.dto.ApiResponse;
import com.hng.model.Profile;
import com.hng.model.ProfileSummary;
import com.hng.parser.QueryParser;
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
            get (this::getAll);
            path("/search", () -> {
                get(this::search);
            });
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
        String minAge    = ctx.queryParam("min_age");
        String maxAge    = ctx.queryParam("max_age");
        String sortBy = ctx.queryParam("sort_by");
        String order = ctx.queryParam("order");
        String page =  ctx.queryParamAsClass("page", String.class).getOrDefault("1");
        String limit = ctx.queryParamAsClass("limit", String.class).getOrDefault("10");
        String minGenderProbability    = ctx.queryParam("min_gender_probability");
        String minCountryProbability    = ctx.queryParam("min_country_probability");
        List<Profile> all = service.getAll(gender, countryId, ageGroup, minAge, maxAge, minGenderProbability, minCountryProbability, sortBy, order, page, limit);
        ctx.json(ApiResponse.successForAll(all, Integer.parseInt(page), Integer.parseInt(limit), service.getTotalProfileCount()));
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

    private void search(Context ctx) throws SQLException {

        String q     = ctx.queryParam("q");
        String page  = ctx.queryParamAsClass("page", String.class).getOrDefault("1");
        String limit = ctx.queryParamAsClass("limit", String.class).getOrDefault("10");

        QueryParser.ParsedQuery filters = QueryParser.parse(q);

        if (!filters.interpreted) {
            ctx.status(400).json(Map.of(
                    "status",  "error",
                    "message", "Unable to interpret query"
            ));
            return;
        }

        List<Profile> profiles = service.getAll(
                filters.gender,
                filters.countryId,
                filters.ageGroup,
                filters.minAge,
                filters.maxAge,
                null,   // minGenderProbability
                null,   // minCountryProbability
                null,   // sortBy
                null,   // order
                page,
                limit
        );

        int total = service.getTotalProfileCount();

        ctx.json(Map.of(
                "data",        profiles,
                "page",        Integer.parseInt(page),
                "limit",       Integer.parseInt(limit),
                "total",       total,
                "interpreted", Map.of(
                        "gender",    filters.gender   != null ? filters.gender   : "",
                        "countryId", filters.countryId != null ? filters.countryId : "",
                        "ageGroup",  filters.ageGroup != null ? filters.ageGroup : "",
                        "minAge",    filters.minAge   != null ? filters.minAge   : "",
                        "maxAge",    filters.maxAge   != null ? filters.maxAge   : ""
                )
        ));

    }
}