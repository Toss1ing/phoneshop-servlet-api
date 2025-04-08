package com.es.phoneshop.web.servlets;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.service.CartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductListAddCartItemServletTest {

    private ProductListAddCartItemServlet servlet;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);

        servlet = new ProductListAddCartItemServlet();

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        servlet.cartService = cartService;

        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testDoPostShouldAddProductToCart() throws ServletException, IOException {
        String quantityStr = "2";
        Long productId = 1L;

        when(request.getParameter("quantity")).thenReturn(quantityStr);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getLocale()).thenReturn(Locale.US);

        servlet.doPost(request, response);

        verify(cartService).add(session, productId, 2);
        verify(response).sendRedirect(contains("/products?success=Product added to cart"));
    }

    @Test
    public void testDoPostInvalidQuantityShouldRedirectWithError() throws ServletException, IOException {
        String quantityStr = "invalid";
        Long productId = 1L;

        when(request.getParameter("quantity")).thenReturn(quantityStr);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("/products?error=Invalid quantity"));
    }

    @Test
    public void testDoPostOutOfStockShouldRedirectWithError() throws ServletException, IOException {
        String quantityStr = "10";
        Long productId = 1L;

        when(request.getParameter("quantity")).thenReturn(quantityStr);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getLocale()).thenReturn(Locale.US);

        doThrow(new OutOfStockException(5)).when(cartService).add(session, productId, 10);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("/products?error=Out of stock"));
    }

    @Test
    public void testDoPostShouldParseQuantityCorrectly() throws ServletException, IOException, ParseException {
        String quantityStr = "2";
        Long productId = 1L;

        when(request.getParameter("quantity")).thenReturn(quantityStr);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getLocale()).thenReturn(Locale.US);


        servlet.doPost(request, response);

        verify(cartService).add(session, productId, 2);
        verify(response).sendRedirect(contains("/products?success=Product added to cart"));
    }
}
