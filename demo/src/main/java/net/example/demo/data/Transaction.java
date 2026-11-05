package net.example.demo.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonPropertyOrder({"date", "time", "description", "vendor", "amount"})
public record Transaction(
        LocalDate date,
        LocalTime time,
        String description,
        String vendor,
        BigDecimal amount
) {}
