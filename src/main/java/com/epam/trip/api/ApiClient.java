package com.epam.trip.api;

import com.epam.trip.exception.ApiException;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Generic HTTP client for making API requests.
 * Handles common functionality like timeouts, retries, and error handling.
 */
public class ApiClient {

    private final OkHttpClient httpClient;
    private final int retryCount;

    public ApiClient(int timeoutSeconds, int retryCount) {
        this.retryCount = retryCount;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Execute a GET request to the specified URL.
     * 
     * @param url The full URL to request
     * @return Response body as string
     * @throws ApiException if the request fails
     */
    public String get(String url) throws ApiException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return executeWithRetry(request);
    }

    /**
     * Execute a GET request with custom headers.
     * 
     * @param url     The full URL to request
     * @param headers Headers to include in the request
     * @return Response body as string
     * @throws ApiException if the request fails
     */
    public String get(String url, Headers headers) throws ApiException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .get()
                .build();

        return executeWithRetry(request);
    }

    /**
     * Execute request with retry logic.
     */
    private String executeWithRetry(Request request) throws ApiException {
        ApiException lastException = null;

        for (int attempt = 0; attempt <= retryCount; attempt++) {
            try {
                Response response = httpClient.newCall(request).execute();

                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No error details";
                    response.close();

                    // Don't retry on client errors (4xx)
                    if (response.code() >= 400 && response.code() < 500) {
                        if (response.code() == 429) {
                            throw ApiException.rateLimited(errorBody);
                        }
                        throw ApiException.httpError(response.code(), errorBody);
                    }

                    // Retry on server errors (5xx)
                    lastException = ApiException.httpError(response.code(), errorBody);
                    continue;
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                response.close();
                return responseBody;

            } catch (IOException e) {
                lastException = ApiException.networkError(e.getMessage(), e);
                // Retry on network errors
                if (attempt < retryCount) {
                    try {
                        Thread.sleep(1000 * (attempt + 1)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ApiException.networkError("Request interrupted", ie);
                    }
                }
            }
        }

        throw lastException != null ? lastException : ApiException.networkError("Request failed after retries", null);
    }
}
