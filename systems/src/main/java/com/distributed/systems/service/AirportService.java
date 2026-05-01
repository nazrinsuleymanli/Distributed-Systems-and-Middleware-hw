package com.distributed.systems.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpEntity<Void> defaultEntity = new HttpEntity<>(
            new HttpHeaders() {{ set("User-Agent", "Mozilla/5.0"); }}
    );

    // ── Weather via wttr.in (no API key needed) ──────────────────────────
    public double getAirportTemperature(String iataCode) throws Exception {
        // Step 1: IATA → lat/lon via airport-data.com (mentioned in homework)
        String airportUrl = "https://airport-data.com/api/ap_info.json?iata=" + iataCode.toUpperCase();
        ResponseEntity<String> airportResp = restTemplate.exchange(
                airportUrl, HttpMethod.GET, defaultEntity, String.class);

        JsonNode airportJson = mapper.readTree(airportResp.getBody());
        double lat = airportJson.path("latitude").asDouble();
        double lon = airportJson.path("longitude").asDouble();

        if (lat == 0.0 && lon == 0.0) {
            throw new RuntimeException("Airport not found: " + iataCode);
        }

        // Step 2: lat/lon → temperature via open-meteo.com (free, no key, real-time)
        String weatherUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m",
                lat, lon
        );
        ResponseEntity<String> weatherResp = restTemplate.exchange(
                weatherUrl, HttpMethod.GET, defaultEntity, String.class);

        JsonNode weatherJson = mapper.readTree(weatherResp.getBody());
        return weatherJson.path("current").path("temperature_2m").asDouble();
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