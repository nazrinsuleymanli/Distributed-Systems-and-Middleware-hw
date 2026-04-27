package com.distributed.systems.service;

import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AirportService {
    private final RestTemplate restTemplate;

    public double evaluateExpression(String expr) {
        Expression expression = new ExpressionBuilder(expr).build();
        return expression.evaluate();
    }

    public double getStockPrice(String symbol) {

        String url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + symbol;

        Map response = restTemplate.getForObject(url, Map.class);

        Map quoteResponse = (Map) response.get("quoteResponse");
        List result = (List) quoteResponse.get("result");

        if (result.isEmpty()) {
            throw new RuntimeException("Invalid stock symbol");
        }

        Map stockData = (Map) result.get(0);

        return Double.parseDouble(stockData.get("regularMarketPrice").toString());
    }
    public double getAirportTemperature(String iata) {

        // 1. Get airport info
        String airportUrl = "https://airport-data.com/api/ap_info.json?iata=" + iata;
        Map airportResponse = restTemplate.getForObject(airportUrl, Map.class);

        if (airportResponse == null || airportResponse.get("latitude") == null) {
            throw new RuntimeException("Invalid airport code");
        }

        double lat = Double.parseDouble(airportResponse.get("latitude").toString());
        double lon = Double.parseDouble(airportResponse.get("longitude").toString());

        // 2. Get weather
        String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude="
                + lat + "&longitude=" + lon + "&current_weather=true";

        Map weatherResponse = restTemplate.getForObject(weatherUrl, Map.class);

        Map currentWeather = (Map) weatherResponse.get("current_weather");

        return Double.parseDouble(currentWeather.get("temperature").toString());
    }
}
