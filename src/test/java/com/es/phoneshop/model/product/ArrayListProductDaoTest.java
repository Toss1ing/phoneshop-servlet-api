package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.exception.ProductExistException;
import com.es.phoneshop.exception.ProductNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {

    private ProductDao productDao;

    @Before
    public void setUp() {
        Currency usd = Currency.getInstance("USD");
        List<Product> initialProducts = new ArrayList<>(Arrays.asList(
                new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "url1"),
                new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "url2"),
                new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(200), usd, 0, "url3"),
                new Product("iphone6", "Apple iPhone 6", null, usd, 20, "url4"))
        );
        productDao = new ArrayListProductDao(initialProducts);
    }

    @Test(expected = NullDataException.class)
    public void testGetProductShouldThrowNullDataExceptionWhenIdIsNull() {
        productDao.getProduct(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProductShouldThrowProductNotFoundExceptionWhenProductNotFound() {
        Long nonExistentId = 5L;
        productDao.getProduct(nonExistentId);
    }

    @Test
    public void testGetProductShouldReturnProductWhenProductExists() {
        Product result = productDao.getProduct(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
    }

    @Test
    public void testFindProductsShouldReturnOnlyValidProducts() {
        List<Product> result = productDao.findProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test(expected = NullDataException.class)
    public void testSaveShouldThrowNullDataExceptionWhenProductIsNull() {
        productDao.save(null);
    }

    @Test
    public void testSaveShouldAssignIdAndSaveWhenProductHasNullId() {
        Currency usd = Currency.getInstance("USD");
        Product newProduct = new Product(null, "nokia", "Nokia 3310", new BigDecimal(50), usd, 20, "url3");
        productDao.save(newProduct);

        assertNotNull(newProduct.getId());
        assertEquals(Long.valueOf(4L), newProduct.getId());
        assertEquals(3, productDao.findProducts().size());
    }

    @Test
    public void testSaveShouldAddProductWhenProductWithUniqueIdIsProvided() {
        Currency usd = Currency.getInstance("USD");
        Product newProduct = new Product(5L, "htc", "HTC One", new BigDecimal(300), usd, 5, "url4");
        productDao.save(newProduct);

        assertEquals(Long.valueOf(5L), newProduct.getId());
        assertEquals(3, productDao.findProducts().size());
    }

    @Test(expected = ProductExistException.class)
    public void testSaveShouldThrowProductExistExceptionWhenProductWithExistingIdIsAdded() {
        Currency usd = Currency.getInstance("USD");
        Product duplicateProduct = new Product(1L, "iphone", "Apple iphone", new BigDecimal(150), usd, 50, "url");

        productDao.save(duplicateProduct);
    }

    @Test
    public void testSaveShouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        Currency usd = Currency.getInstance("USD");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; ++i) {
            executor.submit(() -> {
                Product product = new Product(null, "prod", "Product ", new BigDecimal(100), usd, 10, "url");
                productDao.save(product);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(12, productDao.findProducts().size());
    }

    @Test(expected = NullDataException.class)
    public void testDeleteShouldThrowNullDataExceptionWhenIdIsNull() {
        productDao.delete(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDeleteShouldThrowProductNotFoundExceptionWhenProductNotFound() {
        productDao.delete(5L);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDeleteShouldSuccessfullyRemoveProductWhenProductExists() {
        productDao.delete(1L);
        productDao.getProduct(1L);
    }

}
