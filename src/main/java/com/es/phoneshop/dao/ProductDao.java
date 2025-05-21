package com.es.phoneshop.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.search.SearchMode;
import com.es.phoneshop.model.product.sort.SortField;
import com.es.phoneshop.model.product.sort.SortOrder;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);

    List<Product> findProducts(String query, SortField sortFiled, SortOrder sortOrder);

    void save(Product product);

    void delete(Long id);

    List<Product> findProductsByParams(String description, BigDecimal minPrice, BigDecimal maxPrice, SearchMode searchMode);
}
