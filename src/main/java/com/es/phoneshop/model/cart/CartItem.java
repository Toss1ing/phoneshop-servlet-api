package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serial;
import java.io.Serializable;

public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "[code=" + product.getCode() + ", quantity=" + quantity + "]";
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}
