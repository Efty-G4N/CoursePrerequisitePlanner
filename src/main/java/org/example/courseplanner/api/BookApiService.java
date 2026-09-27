package org.example.courseplanner.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class BookApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BookApiService() {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    // searches Open Library for books matching the given query (e.g. a course name)
    // and returns the parsed result. This method makes a real network call,
    // so it must always be run on a background thread, never on the JavaFX thread.
    public BookSearchResult searchBooks(String query) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://openlibrary.org/search.json?q=" + encodedQuery + "&limit=5";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Open Library API returned status code: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), BookSearchResult.class);
    }


}