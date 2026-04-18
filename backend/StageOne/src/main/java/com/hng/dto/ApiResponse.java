package com.hng.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

// Produces: { "status": "success", "message": "...", "data": {...} }
// message is omitted when null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String status;
    private final String message;
    private final T      data;
    private int count = 0;

    private ApiResponse( String status, int count, String message, T data) {
        this.status  = status;
        this.message = message;
        this.data    = data;
        this.count   = count;
    }

    private ApiResponse(String status, String message, T data) {
        this.status  = status;
        this.message = message;
        this.data    = data;
    }


    public static <T> ApiResponse<T> successForAll(T data, int count) {
        return new ApiResponse<>("success", count, null, data);
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
    public int getCount() {
        return count;
    }
}
