package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NullDataException;
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
                new Product(1L, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "url1"),
                new Product(2L, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "url2"),
                new Product(3L, "sgs3", "Samsung Galaxy S III", new BigDecimal(200), usd, 0, "url3"),
                new Product(4L, "iphone6", "Apple iPhone 6", null, usd, 20, "url4")
        ));
        productDao = new ArrayListProductDao(initialProducts, 5L);
    }

    @Test(expected = NullDataException.class)
    public void testGetProduct_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        productDao.getProduct(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProduct_ShouldThrowRuntimeException_WhenProductNotFound() {
        Long nonExistentId = 5L;
        productDao.getProduct(nonExistentId);
    }

    @Test
    public void testGetProduct_ShouldReturnProduct_WhenProductExists() {
        Product result = productDao.getProduct(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
    }

    //TODO многопоточность в методе getGroup

    @Test
    public void testFindProducts_ShouldReturnOnlyValidProducts() {
        List<Product> result = productDao.findProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test(expected = NullDataException.class)
    public void testSave_ShouldThrowIllegalArgumentException_WhenProductIsNull() {
        productDao.save(null);
    }

    @Test
    public void testSave_ShouldAssignIdAndSave_WhenProductHasNullId() {
        Currency usd = Currency.getInstance("USD");
        Product newProduct = new Product(null, "nokia", "Nokia 3310", new BigDecimal(50), usd, 20, "url3");
        productDao.save(newProduct);

        assertNotNull(newProduct.getId());
        assertEquals(Long.valueOf(5L), newProduct.getId());
        assertEquals(3, productDao.findProducts().size());
    }

    @Test
    public void testSave_ShouldAddProduct_WhenProductWithUniqueIdIsProvided() {
        Currency usd = Currency.getInstance("USD");
        Product newProduct = new Product(5L, "htc", "HTC One", new BigDecimal(300), usd, 5, "url4");
        productDao.save(newProduct);

        assertEquals(Long.valueOf(5L), newProduct.getId());
        assertEquals(3, productDao.findProducts().size());
    }

    @Test
    public void testSave_ShouldBeThreadSafe() throws InterruptedException {
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
    public void testDelete_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        productDao.delete(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDelete_ShouldThrowRuntimeException_WhenProductNotFound() {
        productDao.delete(5L);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDelete_ShouldSuccessfullyRemoveProduct_WhenProductExists() {
        productDao.delete(1L);
        productDao.getProduct(1L);
    }

}
