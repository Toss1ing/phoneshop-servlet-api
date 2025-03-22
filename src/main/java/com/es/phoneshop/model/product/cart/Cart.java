package com.es.phoneshop.model.product.cart;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "cart[" + items + "]";
    }
}
