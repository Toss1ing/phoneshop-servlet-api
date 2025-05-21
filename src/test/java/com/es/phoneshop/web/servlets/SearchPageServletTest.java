package com.es.phoneshop.web.servlets;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.search.SearchMode;
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
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchPageServletTest {

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

    private final SearchPageServlet servlet = new SearchPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        servlet.productService = productService;
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(Locale.US);
    }

    @Test
    public void testDoGetWithValidParamsShouldSetProductsAndSuccess() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn("phone");
        when(request.getParameter("minPrice")).thenReturn("100");
        when(request.getParameter("maxPrice")).thenReturn("1000");
        when(request.getParameter("searchMode")).thenReturn("all");
        when(request.getParameterMap()).thenReturn(Map.of("description", new String[]{"phone"}));
        when(productService.findProductsByParams(
                eq("phone"),
                eq(new BigDecimal("100")),
                eq(new BigDecimal("1000")),
                eq(SearchMode.ALL)))
                .thenReturn(Collections.singletonList(mock(Product.class)));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), anyList());
        verify(request).setAttribute("success", "Product search successfully completed");
        verify(requestDispatcher).forward(request, response);
    }


    @Test
    public void testDoGetWithInvalidMinPriceShouldSetError() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn("test");
        when(request.getParameter("minPrice")).thenReturn("abc");
        when(request.getParameter("maxPrice")).thenReturn("500");
        when(request.getParameter("searchMode")).thenReturn("any");
        when(request.getParameterMap()).thenReturn(Map.of("minPrice", new String[]{"abc"}));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("errors"), argThat(errors ->
                errors instanceof Map && ((Map<?, ?>) errors).containsKey("minPrice")));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithoutSearchAttemptShouldNotSearchProducts() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn(null);
        when(request.getParameter("minPrice")).thenReturn(null);
        when(request.getParameter("maxPrice")).thenReturn(null);
        when(request.getParameter("searchMode")).thenReturn(null);
        when(request.getParameterMap()).thenReturn(Collections.emptyMap());

        servlet.doGet(request, response);

        verify(productService, never()).findProductsByParams(anyString(), any(), any(), any());
        verify(request).setAttribute("products", Collections.emptyList());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testInitShouldInitializeProductDao() throws NoSuchFieldException, IllegalAccessException {
        Field productServiceField = SearchPageServlet.class.getDeclaredField("productService");
        productServiceField.setAccessible(true);

        Object productServiceValue = productServiceField.get(servlet);

        assertNotNull("ProductDao should be initialized", productServiceValue);
    }
}
