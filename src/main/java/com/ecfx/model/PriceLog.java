package com.ecfx.model;

import java.math.BigDecimal;

public class PriceLog implements Comparable<PriceLog> {
    private BigDecimal price;
    private String dateFound;

    public PriceLog() {};
    public PriceLog(BigDecimal price, String dateFound) {
        this.price = price;
        this.dateFound = dateFound;
    };

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDateFound(String dateFound) {
        this.dateFound = dateFound;
    }

    public BigDecimal getPrice() { return price; }
    public String getDateFound() { return dateFound; }

    @Override
    public int compareTo(PriceLog obj) {
        return this.price.compareTo(obj.price);
    }
}
