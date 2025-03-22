package com.es.phoneshop.exception;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends RuntimeException {
    private final Product product;
    private final int stockRequest;
    private final int stockAvailable;

    public OutOfStockException(Product product, int stockRequest, int stockAvailable) {
        this.product = product;
        this.stockRequest = stockRequest;
        this.stockAvailable = stockAvailable;
    }

    private Product getProduct() {
        return product;
    }

    public int getStockRequest() {
        return stockRequest;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

}
