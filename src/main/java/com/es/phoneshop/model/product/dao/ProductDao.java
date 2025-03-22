package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.numeration.SortField;
import com.es.phoneshop.model.product.numeration.SortOrder;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);

    List<Product> findProducts(String query, SortField sortFiled, SortOrder sortOrder);

    void save(Product product);

    void delete(Long id);
}
