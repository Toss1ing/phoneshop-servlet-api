package com.es.phoneshop.web.servlets;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
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

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class CheckoutPageServletTest {

    private CheckoutPageServlet servlet;

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Cart cart;

    @Mock
    private Order order;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);
        servlet = new CheckoutPageServlet();

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        servlet.cartService = cartService;
        servlet.orderService = orderService;

        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(orderService.getOrder(cart, session)).thenReturn(order);
    }

    @Test
    public void testDoGetShouldSetAttributesAndForward() throws ServletException, IOException {
        List<String> paymentMethods = List.of("CASH", "CARD");
        when(orderService.getPaymentMethods()).thenReturn(paymentMethods);
        when(request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("paymentMethods"), eq(paymentMethods));
        verify(request).setAttribute(eq("formData"), any());
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), eq(order));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithValidDataShouldPlaceOrderAndRedirect() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("phone")).thenReturn("+123456789");
        when(request.getParameter("deliveryDate")).thenReturn("2025-04-20");
        when(request.getParameter("deliveryAddress")).thenReturn("123 Main St");
        when(request.getParameter("paymentMethod")).thenReturn("CASH");

        when(order.getSecureId()).thenReturn("secure123");

        servlet.doPost(request, response);

        verify(orderService).placeOrder(order);
        verify(cartService).clear(session);
        verify(response).sendRedirect(contains("/order/overview/secure123"));
    }

    @Test
    public void testDoPostWithInvalidInputReturnToForm() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("phone")).thenReturn("invalidPhone");
        when(request.getParameter("deliveryDate")).thenReturn("2025-04-20");
        when(request.getParameter("deliveryAddress")).thenReturn("123 Main St");
        when(request.getParameter("paymentMethod")).thenReturn("CASH");

        when(orderService.getPaymentMethods()).thenReturn(List.of("CASH", "CARD"));
        when(request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyMap());
        verify(requestDispatcher).forward(request, response);
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    public void testDoPostWithInvalidDateShouldReturnToForm() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("Jane");
        when(request.getParameter("lastName")).thenReturn("Smith");
        when(request.getParameter("phone")).thenReturn("+123456789");
        when(request.getParameter("deliveryDate")).thenReturn("2000-01-20");
        when(request.getParameter("deliveryAddress")).thenReturn("456 Elm St");
        when(request.getParameter("paymentMethod")).thenReturn("CARD");

        when(orderService.getPaymentMethods()).thenReturn(List.of("CASH", "CARD"));
        when(request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(orderService, never()).placeOrder(any());
    }
}
