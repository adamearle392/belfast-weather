package com.adamearle.belfastweather.network.util;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Response;

// Networking utility class derived from Android guide to architecture (MVVM):
// https://github.com/android/architecture-components-samples
// and
// https://developer.android.com/jetpack/guide
public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error) {
        return new ApiErrorResponse<>(!Objects.equals(error.getMessage(), "") ? error.getMessage() : "Unknown error \nCheck network connection");
    }

    public ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();

            if (body == null || response.code() == 204) {
                return new ApiEmptyResponse<>();
            }
            return new ApiSuccessResponse<>(body);
        } else {
            String errorMsg = null;
            try {
                if (response.errorBody() != null) {
                    errorMsg = response.errorBody().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    public static class ApiSuccessResponse<T> extends ApiResponse<T> {

        private T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return this.body;
        }
    }

    public static class ApiErrorResponse<T> extends ApiResponse<T> {

        private String errorMessage;

        ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }

    public static class ApiEmptyResponse<T> extends ApiResponse<T> {
    }
}
