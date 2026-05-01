package com.distributed.systems.service;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AirportService {

    private final RestTemplate restTemplate = new RestTemplate();

    // ── Weather via wttr.in (no API key needed) ──────────────────────────
    public double getAirportTemperature(String iataCode) {
        String url = "https://wttr.in/" + iataCode.toUpperCase() + "?format=j1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "curl/7.68.0");
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

        String json = response.getBody();
        if (json == null) throw new RuntimeException("No weather data for: " + iataCode);

        // Parse manually using Jackson ObjectMapper
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            String tempC = root
                    .path("current_condition")
                    .get(0)
                    .path("temp_C")
                    .asText();
            return Double.parseDouble(tempC);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse weather response: " + e.getMessage());
        }
    }

    // ── Stock price via Yahoo Finance (no API key needed) ─────────────────
    public double getStockPrice(String symbol) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol.toUpperCase()
                + "?interval=1d&range=1d";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<YahooResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, YahooResponse.class);

        YahooResponse body = response.getBody();
        try {
            return body.chart.result.get(0).meta.regularMarketPrice;
        } catch (Exception e) {
            throw new RuntimeException("Stock not found: " + symbol);
        }
    }

    // ── Expression evaluation via exp4j ───────────────────────────────────
    public double evaluateExpression(String expression) {
        // Strip spaces — valid arithmetic needs none, and spaces may be
        // URL-decoded '+' signs that were lost in transit
        String sanitized = expression.replaceAll("\\s+", "");
        return new ExpressionBuilder(sanitized).build().evaluate();
    }

    // ── DTOs ──────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class WttrResponse {
        public List<CurrentCondition> current_condition;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class CurrentCondition {
            public String temp_C;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class YahooResponse {
        public Chart chart;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Chart {
            public List<Result> result;

            @JsonIgnoreProperties(ignoreUnknown = true)
            static class Result {
                public Meta meta;

                @JsonIgnoreProperties(ignoreUnknown = true)
                static class Meta {
                    public double regularMarketPrice;
                }
            }
        }
    }
}