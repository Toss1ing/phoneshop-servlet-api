package com.es.phoneshop.service;

import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface ViewedProductsService {
    void addViewedProduct(HttpSession session, Product product);

    List<Product> getLastViewedProducts(HttpSession session);
}
