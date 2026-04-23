package com.hng.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

// Produces: { "status": "success", "message": "...", "data": {...} }
// message is omitted when null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String status;
    private final String message;
    private final T      data;
    private int limit = 10;
    private int page  = 1;

    public int getTotal() {
        return total;
    }

    private int total  = 1;

    private ApiResponse( String status,  String message, T data, int page, int limit, int total) {
        this.status  = status;
        this.message = message;
        this.data    = data;
        this.page = page;
        this.limit = limit;
        this.total = total;
    }

    private ApiResponse(String status, String message, T data) {
        this.status  = status;
        this.message = message;
        this.data    = data;
    }

    public static <T> ApiResponse<T> successForAll(T data, int page, int limit, int total) {
        return new ApiResponse<>("success", null, data, page, limit, total);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null);
    }

    public String getStatus()  { return status; }
    public String getMessage() { return message; }
    public T      getData()    { return data; }
    public int getLimit() {return limit;}
    public void setLimit(int limit) {this.limit = limit;}
    public int getPage() {return page;}
    public void setPage(int page) {this.page = page;}
}
