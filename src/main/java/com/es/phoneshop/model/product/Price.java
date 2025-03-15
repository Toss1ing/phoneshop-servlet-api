package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.Date;

public class Price {
    private BigDecimal price;
    private Date date;

    public Price() {
    }

    public Price(BigDecimal price, Date date) {
        this.price = price;
        this.date = date;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

}
