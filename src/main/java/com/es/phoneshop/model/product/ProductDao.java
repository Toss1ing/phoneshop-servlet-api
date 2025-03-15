package com.es.phoneshop.model.product;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);

    List<Product> findProducts(final String query, String sortFiled, String sortOrder);

    void save(Product product);

    void delete(Long id);
}
