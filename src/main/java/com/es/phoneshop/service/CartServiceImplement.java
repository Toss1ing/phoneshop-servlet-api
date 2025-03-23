package com.es.phoneshop.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ValidationException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.utility.SessionLockManager;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import jakarta.servlet.http.HttpSession;

import java.util.concurrent.locks.Lock;

public class CartServiceImplement implements CartService {

    private static final String SESSION_ATTRIBUTE = CartServiceImplement.class.getName() + ".cart";
    private static CartServiceImplement INSTANCE;
    protected ProductService productService;

    public static CartServiceImplement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CartServiceImplement();
        }
        return INSTANCE;
    }

    private CartServiceImplement() {
        productService = ProductServiceImplement.getInstance();
    }

    @Override
    public Cart getCart(HttpSession session) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            Cart cart = (Cart) session.getAttribute(SESSION_ATTRIBUTE);

            if (cart == null) {
                session.setAttribute(SESSION_ATTRIBUTE, new Cart());
            }
            return cart;
        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public void add(HttpSession session, Long productId, int quantity) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            if (quantity <= 0) {
                throw new ValidationException("Quantity must be greater than 0");
            }

            Cart cart = (Cart) session.getAttribute(SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(SESSION_ATTRIBUTE, cart);
            }

            Product product = productService.getProduct(productId);

            CartItem cartItem = cart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (cartItem != null) {
                int newQuantity = cartItem.getQuantity() + quantity;
                validateStock(product, newQuantity);
                cartItem.setQuantity(newQuantity);
            } else {
                validateStock(product, quantity);
                cart.getItems().add(new CartItem(product, quantity));
            }

        } finally {
            sessionLock.unlock();
        }
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getStock());
        }
    }

}
