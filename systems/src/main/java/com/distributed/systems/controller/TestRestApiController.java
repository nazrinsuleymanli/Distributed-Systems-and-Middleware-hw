package com.distributed.systems.controller;


import com.distributed.systems.service.AirportService;
import com.distributed.systems.wrapper.ResultWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TestRestApiController {

    private final AirportService airportService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> handleQuery(
            @RequestParam(required = false) String queryAirportTemp,
            @RequestParam(required = false) String queryStockPrice,
            @RequestParam(required = false) String queryEval,
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader
    ) {
        int count = 0;
        if (queryAirportTemp != null) count++;
        if (queryStockPrice  != null) count++;
        if (queryEval        != null) count++;

        if (count != 1) {
            return ResponseEntity.badRequest().body("Exactly one parameter required.");
        }

        try {
            double result;

            if (queryAirportTemp != null) {
                result = airportService.getAirportTemperature(queryAirportTemp);
            } else if (queryStockPrice != null) {
                result = airportService.getStockPrice(queryStockPrice);
            } else {
                result = airportService.evaluateExpression(queryEval);
            }

            boolean wantsXml = acceptHeader.contains("xml");
            if (wantsXml) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(new ResultWrapper(result));
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}
