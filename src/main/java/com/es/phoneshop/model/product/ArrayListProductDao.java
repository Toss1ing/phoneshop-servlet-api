package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.exception.ProductExistException;
import com.es.phoneshop.exception.ProductNotFoundException;

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
    public List<Product> findProducts(final String query, String sortField, String sortOrder) {
        reentrantReadWriteLock.readLock().lock();
        try {
            List<String> keywords = (query == null || query.isBlank())
                    ? List.of()
                    : Arrays.asList(query.toLowerCase().split("\\s+"));

            Comparator<Product> relevanceComparator = this.getRelevanceComparator(keywords, query);
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

    private Comparator<Product> getProductComparator(String sortField, String sortOrder) {
        if (sortField == null) {
            return (p1, p2) -> 0;
        }

        Comparator<Product> comparator = switch (sortField.toLowerCase()) {
            case "price" -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
            case "description" ->
                    Comparator.comparing(Product::getDescription, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> (p1, p2) -> 0;
        };

        return "desc".equalsIgnoreCase(sortOrder) ? comparator.reversed() : comparator;
    }

    private Comparator<Product> getRelevanceComparator(List<String> keywords, String query) {
        if (query != null) {
            return (p1, p2) -> 0;
        }

        return Comparator.comparingInt((Product product) ->
                (int) keywords.stream()
                        .filter(word -> product.getDescription().toLowerCase().contains(word))
                        .count()
        ).reversed();
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
