package com.es.phoneshop.service.impl;

import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.exception.ProductExistException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Price;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.sort.SortField;
import com.es.phoneshop.model.product.sort.SortOrder;
import com.es.phoneshop.service.ProductService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class ProductServiceTest {

    private ProductService productService;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        ResetSingleton();

        productService = ProductServiceImplement.getInstance();

        Currency usd = Currency.getInstance("USD");
        List<Product> initialProducts = new ArrayList<>(Arrays.asList(
                new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100,
                        "url",
                        Arrays.asList(
                                new Price(new BigDecimal(120), Date.valueOf(LocalDate.of(2023, 3, 15))),
                                new Price(new BigDecimal(110), Date.valueOf(LocalDate.of(2023, 5, 20)))
                        )
                ),
                new Product("simc56", "Samsung Galaxy S III", new BigDecimal(70), usd, 20,
                        "url",
                        Arrays.asList(
                                new Price(new BigDecimal(85), Date.valueOf(LocalDate.of(2023, 1, 10))),
                                new Price(new BigDecimal(75), Date.valueOf(LocalDate.of(2023, 2, 15)))
                        )
                ),
                new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 200,
                        "url",
                        Arrays.asList(
                                new Price(new BigDecimal(250), Date.valueOf(LocalDate.of(2023, 1, 20))),
                                new Price(new BigDecimal(230), Date.valueOf(LocalDate.of(2023, 2, 5)))
                        )
                ),
                new Product("sgs3", "Samsung Galaxy S I", null, usd, 5,
                        "url",
                        Arrays.asList(
                                new Price(new BigDecimal(350), Date.valueOf(LocalDate.of(2023, 1, 10))),
                                new Price(new BigDecimal(330), Date.valueOf(LocalDate.of(2023, 2, 18))),
                                new Price(new BigDecimal(310), Date.valueOf(LocalDate.of(2023, 3, 5)))
                        )
                ),
                new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 0,
                        "url",
                        Arrays.asList(
                                new Price(new BigDecimal(170), Date.valueOf(LocalDate.of(2023, 1, 10))),
                                new Price(new BigDecimal(160), Date.valueOf(LocalDate.of(2023, 2, 14)))
                        )
                )
        ));

        initialProducts.forEach(product -> productService.save(product));
    }

    private void ResetSingleton() throws IllegalAccessException, NoSuchFieldException {
        java.lang.reflect.Field instanceField = ProductServiceImplement.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test(expected = NullDataException.class)
    public void testGetProductShouldThrowNullDataExceptionWhenIdIsNull() {
        productService.getProduct(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProductShouldThrowProductNotFoundExceptionWhenProductNotFound() {
        Long nonExistentId = 5L;
        productService.getProduct(nonExistentId);
    }

    @Test
    public void testGetProductShouldReturnProductWhenProductExists() {
        Product result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
    }

    @Test
    public void testFindProductsShouldReturnOnlyValidProducts() {
        List<Product> result = productService.findProducts(null, SortField.NONE, SortOrder.NONE);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test(expected = NullDataException.class)
    public void testSaveShouldThrowNullDataExceptionWhenProductIsNull() {
        productService.save(null);
    }

    @Test
    public void testSaveShouldAssignIdAndSaveWhenProductHasNullId() {
        Currency usd = Currency.getInstance("USD");
        Product newProduct = new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100,
                "url",
                Arrays.asList(
                        new Price(new BigDecimal(90), Date.valueOf(LocalDate.of(2023, 1, 3))),
                        new Price(new BigDecimal(80), Date.valueOf(LocalDate.of(2023, 2, 25)))
                )
        );
        productService.save(newProduct);

        assertNotNull(newProduct.getId());
        assertEquals(Long.valueOf(5L), newProduct.getId());
        assertEquals(4, productService.findProducts(null, SortField.NONE, SortOrder.NONE).size());
    }

    @Test
    public void testSaveShouldAddProductWhenProductWithUniqueIdIsProvided() {
        Currency usd = Currency.getInstance("USD");
        Product newProduct = new Product("test", "test", new BigDecimal(150), usd, 100,
                "url",
                List.of(
                        new Price(new BigDecimal(160), Date.valueOf(LocalDate.of(2023, 2, 14)))
                )
        );
        productService.save(newProduct);

        assertEquals(Long.valueOf(5L), newProduct.getId());
        assertEquals(4, productService.findProducts(null, SortField.NONE, SortOrder.NONE).size());
    }

    @Test(expected = ProductExistException.class)
    public void testSaveShouldThrowProductExistExceptionWhenProductWithExistingIdIsAdded() {
        Currency usd = Currency.getInstance("USD");
        Product duplicateProduct = new Product(1L, "test", "test", new BigDecimal(150), usd, 100,
                "url",
                List.of(
                        new Price(new BigDecimal(160), Date.valueOf(LocalDate.of(2023, 2, 14)))
                )
        );
        productService.save(duplicateProduct);
    }

    @Test
    public void testSaveShouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        Currency usd = Currency.getInstance("USD");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; ++i) {
            executor.submit(() -> {
                Product product = new Product("test", "test", new BigDecimal(150), usd, 100,
                        "url",
                        List.of(
                                new Price(new BigDecimal(160), Date.valueOf(LocalDate.of(2023, 2, 14)))
                        )
                );
                productService.save(product);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(13, productService.findProducts(null, SortField.NONE, SortOrder.NONE).size());
    }

    @Test(expected = NullDataException.class)
    public void testDeleteShouldThrowNullDataExceptionWhenIdIsNull() {
        productService.delete(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDeleteShouldThrowProductNotFoundExceptionWhenProductNotFound() {
        productService.delete(10L);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDeleteShouldSuccessfullyRemoveProductWhenProductExists() {
        productService.delete(1L);
        productService.getProduct(1L);
    }

    @Test
    public void testSortProductsByPriceAsc() {
        List<Product> products = productService.findProducts(null, SortField.PRICE, SortOrder.ASC);

        assertNotNull(products);
        assertEquals(3, products.size());
        assertTrue(products.get(0).getPrice().compareTo(products.get(1).getPrice()) <= 0);
        assertTrue(products.get(1).getPrice().compareTo(products.get(2).getPrice()) <= 0);
    }

    @Test
    public void testSortProductsByPriceDesc() {
        List<Product> products = productService.findProducts(null, SortField.PRICE, SortOrder.DESC);

        assertNotNull(products);
        assertEquals(3, products.size());

        assertTrue(products.get(0).getPrice().compareTo(products.get(1).getPrice()) >= 0);
        assertTrue(products.get(1).getPrice().compareTo(products.get(2).getPrice()) >= 0);
    }

    @Test
    public void testSortProductsByDescriptionAsc() {
        List<Product> products = productService.findProducts(null, SortField.DESCRIPTION, SortOrder.ASC);

        assertNotNull(products);
        assertEquals(3, products.size());
        assertTrue(products.get(0).getDescription().compareTo(products.get(1).getDescription()) <= 0);
        assertTrue(products.get(1).getDescription().compareTo(products.get(2).getDescription()) <= 0);
    }

    @Test
    public void testSortProductsByDescriptionDesc() {
        List<Product> products = productService.findProducts(null, SortField.DESCRIPTION, SortOrder.DESC);

        assertNotNull(products);
        assertEquals(3, products.size());
        assertTrue(products.get(0).getDescription().compareTo(products.get(1).getDescription()) >= 0);
        assertTrue(products.get(1).getDescription().compareTo(products.get(2).getDescription()) >= 0);
    }

    @Test
    public void testSingletonInstance() {
        ProductServiceImplement instance1 = ProductServiceImplement.getInstance();
        ProductServiceImplement instance2 = ProductServiceImplement.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testSearchAndSortByPriceAsc() {
        List<Product> result = productService.findProducts("Samsung", SortField.PRICE, SortOrder.ASC);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.get(0).getPrice().compareTo(result.get(1).getPrice()) <= 0);
        assertTrue(result.get(1).getPrice().compareTo(result.get(2).getPrice()) <= 0);

        assertTrue(result.stream().allMatch(p -> p.getDescription().toLowerCase().contains("samsung")));
    }

    @Test
    public void testSearchAndSortByPriceDesc() {
        List<Product> result = productService.findProducts("Samsung", SortField.PRICE, SortOrder.DESC);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.get(0).getPrice().compareTo(result.get(1).getPrice()) >= 0);
        assertTrue(result.get(1).getPrice().compareTo(result.get(2).getPrice()) >= 0);

        assertTrue(result.stream().allMatch(p -> p.getDescription().toLowerCase().contains("samsung")));
    }

    @Test
    public void testSearchAndSortByDescriptionAsc() {
        List<Product> result = productService.findProducts("Galaxy", SortField.DESCRIPTION, SortOrder.ASC);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.get(0).getDescription().compareTo(result.get(1).getDescription()) <= 0);
        assertTrue(result.get(1).getDescription().compareTo(result.get(2).getDescription()) <= 0);

        assertTrue(result.stream().allMatch(p -> p.getDescription().toLowerCase().contains("galaxy")));
    }

    @Test
    public void testSearchAndSortByDescriptionDesc() {
        List<Product> result = productService.findProducts("Galaxy", SortField.DESCRIPTION, SortOrder.DESC);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.get(0).getDescription().compareTo(result.get(1).getDescription()) >= 0);
        assertTrue(result.get(1).getDescription().compareTo(result.get(2).getDescription()) >= 0);

        assertTrue(result.stream().allMatch(p -> p.getDescription().toLowerCase().contains("galaxy")));
    }

    @Test
    public void testSearchShouldReturnProductsContainingSearchQuery() {
        List<Product> result = productService.findProducts("I", SortField.NONE, SortOrder.NONE);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().allMatch(p -> p.getDescription().toLowerCase().contains("samsung")));
    }

}