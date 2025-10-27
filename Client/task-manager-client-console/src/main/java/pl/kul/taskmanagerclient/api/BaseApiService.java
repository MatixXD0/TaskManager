package pl.kul.taskmanagerclient.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class BaseApiService {
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;

    protected BaseApiService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    protected HttpRequest createRequest(String url, String path, String method, Object body) throws IOException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url + (path != null ? path : "")))
                .header("Content-Type", "application/json");

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return builder.build();
    }

    protected String buildUrlWithQuery(String baseUrl, String path, Map<String, String> queryParams) {
        StringBuilder url = new StringBuilder(baseUrl + (path != null ? path : ""));
        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?");
            queryParams.forEach((key, value) ->
                    url.append(key).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append("&"));
            url.deleteCharAt(url.length() - 1);
        }
        return url.toString();
    }

    protected <T> T sendRequest(String url, String path, String method, Object body, TypeReference<T> typeReference)
            throws IOException, InterruptedException {
        HttpRequest request = createRequest(url, path, method, body);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, typeReference);
    }

    protected <T> T handleResponse(HttpResponse<String> response, TypeReference<T> typeReference) throws IOException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (typeReference == null || response.body() == null || response.body().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(response.body(), typeReference);
        }
        throw new RuntimeException("Unexpected error: " + response.statusCode() + " - " + response.body());
    }

}
