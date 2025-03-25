package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpSession;

public interface CartService {
    Cart getCart(HttpSession session);

    void add(HttpSession session, Long productId, int quantity);
}
