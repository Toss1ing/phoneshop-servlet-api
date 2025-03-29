package com.es.phoneshop.web;

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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest {

    private MiniCartServlet servlet;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private Cart cart;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);

        servlet = new MiniCartServlet();

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        servlet.cartService = cartService;

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/WEB-INF/pages/miniCart.jsp")).thenReturn(requestDispatcher);

        when(cartService.getCart(session)).thenReturn(cart);
    }

    @Test
    public void testDoGetShouldIncludeMiniCartPage() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(cartService).getCart(session);

        verify(request).setAttribute("cart", cart);

        verify(requestDispatcher).include(request, response);
    }
}
