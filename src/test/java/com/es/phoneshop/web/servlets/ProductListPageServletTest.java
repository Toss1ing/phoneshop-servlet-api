package com.es.phoneshop.web.servlets;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.sort.SortField;
import com.es.phoneshop.model.product.sort.SortOrder;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private ProductDao productService;

    @Mock
    private ServletConfig config;

    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        servlet.productService = productService;
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGetShouldSetProductsAttributeAndForwardRequest() throws ServletException, IOException {
        String query = "test";
        SortField sortField = SortField.DESCRIPTION;
        SortOrder sortOrder = SortOrder.ASC;

        when(request.getParameter("query")).thenReturn(query);
        when(request.getParameter("sort")).thenReturn(String.valueOf(sortField));
        when(request.getParameter("order")).thenReturn(String.valueOf(sortOrder));

        when(productService.findProducts(query, sortField, sortOrder)).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetShouldForwardToProductListPage() throws ServletException, IOException {
        when(request.getParameter("query")).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("order")).thenReturn(null);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/pages/productList.jsp");
    }

    @Test
    public void testInitShouldInitializeProductDao() throws NoSuchFieldException, IllegalAccessException {
        Field productServiceField = ProductListPageServlet.class.getDeclaredField("productService");
        productServiceField.setAccessible(true);

        Object productServiceValue = productServiceField.get(servlet);

        assertNotNull("ProductDao should be initialized", productServiceValue);
    }

}

