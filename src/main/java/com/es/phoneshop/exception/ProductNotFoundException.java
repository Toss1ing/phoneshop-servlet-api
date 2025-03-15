package com.es.phoneshop.exception;

public class ProductNotFoundException extends RuntimeException {

    private final Long productId;

    public Long getProductId() {
        return productId;
    }

    public ProductNotFoundException(Long productId) {
        super("Product with id " + productId + " not found");
        this.productId = productId;
    }

}
