package com.es.phoneshop.web;

import jakarta.servlet.ServletConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;

    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet_ShouldSetProductsAttributeAndForwardRequest() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());
    }

    @Test
    public void testDoGet_RedirectToCorrectPage() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/pages/productList.jsp");
    }

    @Test
    public void testInit_ShouldInitializeProductDao() throws ServletException, NoSuchFieldException, IllegalAccessException {
        Field productDaoField = ProductListPageServlet.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);

        Object productDaoValue = productDaoField.get(servlet);

        assertNotNull(productDaoValue);
    }

}