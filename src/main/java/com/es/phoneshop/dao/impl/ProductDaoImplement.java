package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.GenericDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.sort.SortField;
import com.es.phoneshop.model.product.sort.SortOrder;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ProductDaoImplement extends GenericDao<Product> implements ProductDao {

    private static ProductDaoImplement INSTANCE;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    private ProductDaoImplement() {
        entities = new ArrayList<>();
        countId = 0L;
    }

    public static synchronized ProductDaoImplement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProductDaoImplement();
        }
        return INSTANCE;
    }

    @Override
    public Product getProduct(Long id) {
        if (id == null) {
            throw new NullDataException("Product id cannot be null");
        }

        reentrantReadWriteLock.readLock().lock();

        try {
            return entities.stream()
                    .filter(entity -> entity.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Entity not found"));
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        reentrantReadWriteLock.readLock().lock();
        try {
            List<String> keywords = (StringUtils.isBlank(query))
                    ? List.of()
                    : Arrays.asList(query.trim().toLowerCase().split("\\s+"));

            Comparator<Product> relevanceComparator = this.getRelevanceComparator(keywords, sortField, sortOrder);
            Comparator<Product> productComparator = this.getProductComparator(sortField, sortOrder);

            return entities.stream()
                    .filter(entity -> entity.getStock() > 0 && entity.getPrice() != null)
                    .filter(entity -> keywords.isEmpty() || keywords.stream()
                            .anyMatch(word -> entity.getDescription().toLowerCase().contains(word)))
                    .sorted(relevanceComparator.thenComparing(productComparator))
                    .collect(Collectors.toList());
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    private Comparator<Product> getProductComparator(SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator = switch (sortField) {
            case PRICE -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
            case DESCRIPTION ->
                    Comparator.comparing(Product::getDescription, Comparator.nullsLast(Comparator.naturalOrder()));
            case NONE -> (p1, p2) -> 0;
        };

        if (sortOrder == SortOrder.NONE) {
            return comparator;
        }

        return sortOrder == SortOrder.DESC ? comparator.reversed() : comparator;
    }

    private Comparator<Product> getRelevanceComparator(List<String> keywords, SortField sortField, SortOrder sortOrder) {
        if (!sortField.equals(SortField.NONE) || !sortOrder.equals(SortOrder.NONE)) {
            return (p1, p2) -> 0;
        }

        return Comparator
                .comparingInt((Product product) ->
                        (int) keywords.stream()
                                .filter(word -> Arrays.stream(product.getDescription().toLowerCase().split("\\s+"))
                                        .anyMatch(descriptionWord -> descriptionWord.contains(word)))
                                .count()
                )
                .thenComparingDouble((Product product) -> {
                    long matchedWords = keywords.stream()
                            .filter(word -> Arrays.stream(product.getDescription().toLowerCase().split("\\s+"))
                                    .anyMatch(descriptionWord -> descriptionWord.contains(word)))
                            .count();
                    int totalWords = product.getDescription().split("\\s+").length;
                    return (double) matchedWords / totalWords;
                })
                .reversed();
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new NullDataException("Product id cannot be null");
        }

        reentrantReadWriteLock.writeLock().lock();

        try {
            boolean removed = entities.removeIf(entity -> id.equals(entity.getId()));

            if (!removed) {
                throw new NotFoundException("Entity not found");
            }
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    protected Long getId(Product product) {
        return product.getId();
    }

    @Override
    protected void setId(Product product) {
        product.setId(countId++);
    }
}
