package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import jakarta.servlet.RequestDispatcher;
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
import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {

    private CartPageServlet servlet;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Cart cart;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);

        servlet = new CartPageServlet();

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        servlet.cartService = cartService;

        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
    }

    @Test
    public void testDoGetShouldLoadCartPage() throws ServletException, IOException {
        when(request.getRequestDispatcher("/WEB-INF/pages/cart.jsp")).thenReturn(mock(RequestDispatcher.class));

        servlet.doGet(request, response);

        verify(cartService).getCart(session);
        verify(request).setAttribute("cart", cart);
        verify(request.getRequestDispatcher("/WEB-INF/pages/cart.jsp")).forward(request, response);
    }

    @Test
    public void testDoPostShouldUpdateCartSuccessfully() throws ServletException, IOException {
        String[] productIds = {"1"};
        String[] quantities = {"5"};
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        when(request.getLocale()).thenReturn(Locale.US);

        servlet.doPost(request, response);

        verify(cartService).update(session, 1L, 5);
        verify(response).sendRedirect(contains("/cart?success=Cart updated successfully"));
    }

    @Test
    public void testDoPostShouldHandleInvalidQuantity() throws ServletException, IOException {
        String[] productIds = {"1"};
        String[] quantities = {"invalid"};
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);

        servlet.doPost(request, response);

        verify(cartService, never()).update(any(), anyLong(), anyInt());
        verify(response).sendRedirect(contains("/cart"));
        verify(session).setAttribute(eq("cartErrors"), any());
    }

    @Test
    public void testDoPostShouldHandleOutOfStock() throws ServletException, IOException {
        String[] productIds = {"1"};
        String[] quantities = {"10"};
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        when(request.getLocale()).thenReturn(Locale.US);

        doThrow(new OutOfStockException(5)).when(cartService).update(session, 1L, 10);

        servlet.doPost(request, response);

        verify(cartService).update(session, 1L, 10);
        verify(session).setAttribute(eq("cartErrors"), any());
    }

    @Test
    public void testDoPostShouldRedirectWhenNoProductsToUpdate() throws ServletException, IOException {
        when(request.getParameterValues("productId")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("/cart?success=Add products to the cart"));
    }
}
