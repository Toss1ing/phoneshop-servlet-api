package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.cart.Cart;
import jakarta.servlet.http.HttpSession;

public interface CartDao {
    Cart getCart(HttpSession session);

    void add(HttpSession session, Long productId, int quantity);
}
