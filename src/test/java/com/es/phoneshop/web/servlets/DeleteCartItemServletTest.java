package com.es.phoneshop.web.servlets;

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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {

    private DeleteCartItemServlet servlet;

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

        servlet = new DeleteCartItemServlet();

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        servlet.cartService = cartService;

        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testDoPostShouldRemoveProductFromCartAndRedirect() throws ServletException, IOException {
        Long productId = 1L;
        when(request.getPathInfo()).thenReturn("/" + productId);

        servlet.doPost(request, response);

        verify(cartService).delete(session, productId);
        verify(response).sendRedirect(contains("/cart?success=Product removed"));
    }

    @Test
    public void testParseProductIdShouldReturnCorrectId() {
        String pathInfo = "/1";
        when(request.getPathInfo()).thenReturn(pathInfo);

        Long productId = servlet.parseProductId(request);

        assertEquals(Long.valueOf(1), productId);
    }

    @Test
    public void testDoPostShouldHandleInvalidProductId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/invalid");

        try {
            servlet.doPost(request, response);
        } catch (NumberFormatException e) {
            verify(response, never()).sendRedirect(anyString());
        }
    }
}
