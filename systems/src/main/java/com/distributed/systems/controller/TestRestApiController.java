package com.distributed.systems.controller;


import com.distributed.systems.service.AirportService;
import com.distributed.systems.wrapper.ResultWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class TestRestApiController {

    private final AirportService airportService;

    @GetMapping(value = {"","/"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> handleQuery(
            @RequestParam(required = false) String queryAirportTemp,
            @RequestParam(required = false) String queryStockPrice,
            @RequestParam(required = false) String queryEval
    ) {
        int count = 0;
        if (queryAirportTemp != null) count++;
        if (queryStockPrice != null) count++;
        if (queryEval != null) count++;

        if (count != 1) {
            return ResponseEntity.badRequest().body("Only one parameter allowed");
        }

        try {
            // ✈️ Airport temperature
            if (queryAirportTemp != null) {
                double temp = airportService.getAirportTemperature(queryAirportTemp);
                return ResponseEntity.ok(new ResultWrapper(temp));
            }

            // 📈 Stock price
            if (queryStockPrice != null) {
                double price = airportService.getStockPrice(queryStockPrice);
                return ResponseEntity.ok(new ResultWrapper(price));
            }

            // 🧮 Expression
            if (queryEval != null) {
                double result = airportService.evaluateExpression(queryEval);
                return ResponseEntity.ok(new ResultWrapper(result));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }

        return ResponseEntity.badRequest().build();
    }


}
