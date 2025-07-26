package com.alura.challenalura.service;

import com.alura.challenalura.dto.BookDTO;
import com.alura.challenalura.dto.BookResponseDTO;
import com.alura.challenalura.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
public class GutendexService {
    private static final String API_URL = "https://gutendex.com/books/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GutendexService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public BookDTO searchBookByTitle(String title) {
        BookResponseDTO response = searchBooks(title, null, null, null, null);
        if (response.getResults() != null && !response.getResults().isEmpty()) {
            return response.getResults().get(0);
        }
        throw new ApiException("No se encontraron libros con ese título");
    }

    public BookResponseDTO searchBooks(String title, List<String> languages, Integer authorYearStart,
                                       Integer authorYearEnd, String sort) {
        try {
            StringBuilder urlBuilder = new StringBuilder(API_URL);

            boolean hasParams = false;
            if (title != null && !title.isBlank()) {
                urlBuilder.append(hasParams ? "&" : "?")
                        .append("search=")
                        .append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                hasParams = true;
            }

            if (languages != null && !languages.isEmpty()) {
                urlBuilder.append(hasParams ? "&" : "?")
                        .append("languages=")
                        .append(String.join(",", languages));
                hasParams = true;
            }

            if (authorYearStart != null) {
                urlBuilder.append(hasParams ? "&" : "?")
                        .append("author_year_start=")
                        .append(authorYearStart);
                hasParams = true;
            }

            if (authorYearEnd != null) {
                urlBuilder.append(hasParams ? "&" : "?")
                        .append("author_year_end=")
                        .append(authorYearEnd);
                hasParams = true;
            }

            if (sort != null && !sort.isBlank()) {
                urlBuilder.append(hasParams ? "&" : "?")
                        .append("sort=")
                        .append(sort);
            }

            String searchUrl = urlBuilder.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), BookResponseDTO.class);
            } else if (response.statusCode() == 429) {
                throw new ApiException("Demasiadas solicitudes. Por favor espere antes de intentar nuevamente.");
            } else {
                throw new ApiException("Error en la API: " + response.statusCode());
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error al buscar libros: " + e.getMessage());
        }
    }

    public BookResponseDTO getBooksByPageUrl(String pageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pageUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), BookResponseDTO.class);
            }
            throw new ApiException("Error al obtener página: " + response.statusCode());
        } catch (Exception e) {
            throw new ApiException("Error al obtener página de resultados: " + e.getMessage());
        }
    }
}