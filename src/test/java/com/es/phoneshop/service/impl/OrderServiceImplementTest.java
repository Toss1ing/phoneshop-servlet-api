package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderServiceImplementTest {

    private OrderServiceImplement orderServiceImplement;

    @Mock
    private OrderDao orderDao;

    @Mock
    private Cart cart;

    @Mock
    private HttpSession session;

    @Mock
    private CartItem cartItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        orderServiceImplement = new OrderServiceImplement();
        orderServiceImplement.orderServiceDao = orderDao;

        when(session.getId()).thenReturn("newSessionId");
    }

    @Test
    public void testPlaceOrderShouldGenerateSecureIdAndSaveOrder() {
        Order order = new Order();
        order.setSubtotal(new BigDecimal("500"));
        order.setTotalQuantity(5);
        order.setDeliveryCost(new BigDecimal("100"));
        order.setTotalPrice(new BigDecimal("600"));

        doNothing().when(orderDao).save(order);

        orderServiceImplement.placeOrder(order);

        assertNotNull("Secure ID should be generated", order.getSecureId());
        verify(orderDao).save(order);
    }

    @Test
    public void testGetOrderShouldCreateOrderFromCart() {
        when(cart.getTotalQuantity()).thenReturn(5);
        when(cart.getTotalPrice()).thenReturn(new BigDecimal("500"));
        when(cart.getItems()).thenReturn(List.of(cartItem));

        Order order = orderServiceImplement.getOrder(cart, session);

        assertNotNull("Order should not be null", order);
        assertEquals("Total quantity should be same as cart's total quantity", 5, order.getTotalQuantity());
        assertEquals("Subtotal should be equal to cart's total price", new BigDecimal("500"), order.getSubtotal());
        assertEquals("Delivery cost should be 100", new BigDecimal("100"), order.getDeliveryCost());
        assertEquals("Total price should be subtotal + delivery cost", new BigDecimal("600"), order.getTotalPrice());

    }

    @Test
    public void testPlaceOrderShouldGenerateUniqueSecureId() {
        Order order1 = new Order();
        Order order2 = new Order();

        orderServiceImplement.placeOrder(order1);
        String firstSecureId = order1.getSecureId();

        orderServiceImplement.placeOrder(order2);
        String secondSecureId = order2.getSecureId();

        assertNotEquals("Secure IDs should be unique", firstSecureId, secondSecureId);
    }

    @Test
    public void testGetPaymentMethodsShouldReturnAllPaymentMethods() {
        List<String> paymentMethods = orderServiceImplement.getPaymentMethods();

        assertNotNull("Payment methods should not be null", paymentMethods);
        assertTrue("Payment methods should contain 'CREDIT_CARD'", paymentMethods.contains("CREDIT_CARD"));
        assertTrue("Payment methods should contain 'CASH'", paymentMethods.contains("CASH"));
    }

    @Test
    public void testCalculateDeliveryCostShouldReturnFixedCost() {
        BigDecimal deliveryCost = orderServiceImplement.calculateDeliveryCost();

        assertEquals("Delivery cost should be 100", new BigDecimal("100"), deliveryCost);
    }
}
