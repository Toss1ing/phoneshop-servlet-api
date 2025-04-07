package com.es.phoneshop.web.servlets;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletContextEvent servletContextEvent;

    @Mock
    private ProductDao productService;

    @InjectMocks
    private DemoDataServletContextListener demoDataServletContextListener;

    @Before
    public void setUp() {
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void testContextInitializedWithEnableDemoDataListenerFalse() {
        when(servletContext.getInitParameter("enableDemoDataListener")).thenReturn("false");

        demoDataServletContextListener.contextInitialized(servletContextEvent);

        verify(productService, times(0)).save(any(Product.class));
    }

    @Test
    public void testLoadProducts() {
        List<Product> products = demoDataServletContextListener.loadProducts();

        assert !products.isEmpty();

        Product product = products.get(0);
        assert product.getCode().equals("sgs");
        assert product.getDescription().equals("Samsung Galaxy S");
    }

    @Test
    public void testLoadProductsContent() {
        List<Product> products = demoDataServletContextListener.loadProducts();

        Product product = products.get(0);
        assert product.getDescription().equals("Samsung Galaxy S");

        assert product.getPriceHistory().get(0).getPrice().compareTo(new BigDecimal(120)) == 0;
        assert product.getPriceHistory().get(1).getPrice().compareTo(new BigDecimal(110)) == 0;
    }

    @Test
    public void testProductSaveCheckProductData() {
        List<Product> products = demoDataServletContextListener.loadProducts();

        products.forEach(product -> productService.save(product));

        verify(productService, times(1)).save(products.get(0));
    }

}
