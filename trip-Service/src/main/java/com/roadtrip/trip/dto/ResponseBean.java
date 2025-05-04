package com.roadtrip.trip.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBean<T> {
    private int status;
    private String message;
    private T data;
    private long timestamp;

    public static <T> ResponseBean<T> success(T data) {
        return ResponseBean.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResponseBean<T> success(String message, T data) {
        return ResponseBean.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResponseBean<T> error(int status, String message) {
        return ResponseBean.<T>builder()
                .status(status)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}