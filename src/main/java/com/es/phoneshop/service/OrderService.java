package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart, HttpSession session);
    void placeOrder(Order order);
    List<String> getPaymentMethods();
}
