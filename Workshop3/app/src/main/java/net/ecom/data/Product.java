package net.ecom.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;

@JsonPropertyOrder({"sku", "name", "category", "price"})
public record Product(String sku, String name, String category, BigDecimal price) {}
