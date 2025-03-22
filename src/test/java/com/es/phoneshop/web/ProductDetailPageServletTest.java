package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.ViewedProductsServiceImplement;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProductDetailPageServletTest {

    private ProductDetailPageServlet servlet;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private ViewedProductsServiceImplement viewedProductsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private Product product;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);

        servlet = new ProductDetailPageServlet();

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        servlet.productService = productService;
        servlet.cartService = cartService;
        servlet.viewedProductsService = viewedProductsService;

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        when(productService.getProduct(1L)).thenReturn(product);
    }

    @Test
    public void testDoGetShouldLoadProductPage() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(productService).getProduct(1L);

        verify(viewedProductsService).addViewedProduct(session, product);

        verify(request).setAttribute(eq("product"), eq(product));
        verify(request).setAttribute(eq("cart"), any());
        verify(request).setAttribute(eq("viewedProducts"), any());

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostShouldAddProductToCart() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("2");
        when(request.getLocale()).thenReturn(Locale.US);

        servlet.doPost(request, response);

        verify(cartService).add(session, 1L, 2);

        verify(response).sendRedirect(contains("?success=Product added to cart"));
    }

    @Test
    public void testDoPostInvalidQuantityShouldRedirectWithError() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("invalid");
        when(request.getLocale()).thenReturn(Locale.US);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("?error=Invalid quantity"));
    }

    @Test
    public void testDoPostOutOfStockShouldRedirectWithError() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getLocale()).thenReturn(Locale.US);
        doThrow(new OutOfStockException(product, 10, 5)).when(cartService).add(session, 1L, 10);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("?error=Out of stock"));
    }

    @Test
    public void testParseProductIdShouldReturnCorrectId() {
        Long productId = servlet.parseProductId(request);
        assertEquals(Long.valueOf(1), productId);
    }

}
