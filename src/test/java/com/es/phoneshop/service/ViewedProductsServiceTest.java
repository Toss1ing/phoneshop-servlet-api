package com.es.phoneshop.service;

import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;

public class ViewedProductsServiceTest {
    private ViewedProductsServiceImplement viewedProductsService;
    private static final String SESSION_ATTRIBUTE = ViewedProductsServiceImplement.class.getName() + ".viewedProducts";

    @Mock
    private HttpSession session;

    @Mock
    private Product product1;

    @Mock
    private Product product2;

    @Mock
    private Product product3;

    @Mock
    private Product product4;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewedProductsService = new ViewedProductsServiceImplement();

        Mockito.when(product1.getCode()).thenReturn("sgs");
        Mockito.when(product2.getCode()).thenReturn("simc56");
        Mockito.when(product3.getCode()).thenReturn("sgs2");
        Mockito.when(product4.getCode()).thenReturn("sgs3");

        Mockito.when(session.getId()).thenReturn("session");

        Mockito.doAnswer(invocation -> {
            Object value = invocation.getArgument(1);
            Mockito.when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(value);
            return null;
        }).when(session).setAttribute(Mockito.eq(SESSION_ATTRIBUTE), Mockito.any());
    }


    @Test
    public void testAddViewedProductShouldAddProductToSession() {
        viewedProductsService.addViewedProduct(session, product1);

        List<Product> viewedProducts = (List<Product>) session.getAttribute(SESSION_ATTRIBUTE);
        assertNotNull(viewedProducts);
        assertEquals(1, viewedProducts.size());
        assertEquals(product1, viewedProducts.get(0));

    }

    @Test
    public void testAddViewedProductShouldNotDuplicateProduct() {
        viewedProductsService.addViewedProduct(session, product1);
        viewedProductsService.addViewedProduct(session, product2);
        viewedProductsService.addViewedProduct(session, product1);

        List<Product> viewedProducts = viewedProductsService.getLastViewedProducts(session);

        assertEquals(2, viewedProducts.size());
        assertEquals(product1, viewedProducts.get(0));
        assertEquals(product2, viewedProducts.get(1));

    }

    @Test
    public void testAddViewedProductShouldMaintainMaxSize() {
        viewedProductsService.addViewedProduct(session, product1);
        viewedProductsService.addViewedProduct(session, product2);
        viewedProductsService.addViewedProduct(session, product3);
        viewedProductsService.addViewedProduct(session, product4);

        List<Product> viewedProducts = viewedProductsService.getLastViewedProducts(session);

        assertEquals(3, viewedProducts.size());
        assertEquals(product4, viewedProducts.get(0));
        assertEquals(product3, viewedProducts.get(1));
        assertEquals(product2, viewedProducts.get(2));
    }

    @Test
    public void testGetLastViewedProductsShouldReturnEmptyListIfNoProducts() {
        Mockito.when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(null);

        List<Product> viewedProducts = viewedProductsService.getLastViewedProducts(session);

        assertNotNull(viewedProducts);
        assertTrue(viewedProducts.isEmpty());
    }

}
