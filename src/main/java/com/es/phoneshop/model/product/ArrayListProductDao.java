package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.exception.ProductExistException;
import com.es.phoneshop.exception.ProductNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {

    private static ArrayListProductDao instance;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private Long idCounter;
    private final List<Product> products;

    private ArrayListProductDao() {
        idCounter = 0L;
        products = new ArrayList<>();
    }

    public static synchronized ArrayListProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    @Override
    public Product getProduct(Long id) {
        if (id == null) {
            throw new NullDataException("Product id cannot be null");
        }

        reentrantReadWriteLock.readLock().lock();

        try {
            return products.stream()
                    .filter(product -> product.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException(id));
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
                    : Arrays.asList(query.toLowerCase().split("\\s+"));

            Comparator<Product> relevanceComparator = this.getRelevanceComparator(keywords, sortField, sortOrder);
            Comparator<Product> productComparator = this.getProductComparator(sortField, sortOrder);

            return products.stream()
                    .filter(product -> product.getStock() > 0 && product.getPrice() != null)
                    .filter(product -> keywords.isEmpty() || keywords.stream()
                            .anyMatch(word -> product.getDescription().toLowerCase().contains(word)))
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
                                .flatMap(word -> Arrays.stream(product.getDescription().toLowerCase().split("\\s+"))
                                        .filter(descriptionWord -> descriptionWord.contains(word)))
                                .count()
                )
                .reversed()
                .thenComparingInt(product ->
                        product.getDescription().split("\\s+").length
                );
    }

    @Override
    public void save(Product product) {
        reentrantReadWriteLock.writeLock().lock();

        try {
            if (product == null) {
                throw new NullDataException("Product cannot be null");
            }
            if (product.getId() == null) {
                product.setId(idCounter++);
                products.add(product);
            } else {
                boolean productExists = products.stream()
                        .anyMatch(p -> product.getId().equals(p.getId()));
                if (productExists) {
                    throw new ProductExistException("Product with id " + product.getId() + " already exists");
                }
                products.add(product);
            }
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new NullDataException("Product id cannot be null");
        }

        reentrantReadWriteLock.writeLock().lock();

        try {
            boolean removed = products.removeIf(product -> id.equals(product.getId()));

            if (!removed) {
                throw new ProductNotFoundException(id);
            }
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

}
