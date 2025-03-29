package com.es.phoneshop.model.cart;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<CartItem> items;

    private int totalQuantity;

    private BigDecimal totalPrice;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalQuantity = 0;
        this.totalPrice = BigDecimal.ZERO;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "cart[" + items + "]";
    }
}
