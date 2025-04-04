package com.es.phoneshop.service;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.sort.SortField;
import com.es.phoneshop.model.product.sort.SortOrder;

import java.util.List;

public interface ProductService {
    Product getProduct(Long id);

    List<Product> findProducts(String query, SortField sortFiled, SortOrder sortOrder);

    void save(Product product);

    void delete(Long id);
}
