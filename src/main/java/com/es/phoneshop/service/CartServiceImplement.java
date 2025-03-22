package com.es.phoneshop.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.utility.SessionLockManager;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import jakarta.servlet.http.HttpSession;

import java.util.concurrent.locks.Lock;

public class CartServiceImplement implements CartService {

    private static final String SESSION_ATTRIBUTE = CartServiceImplement.class.getName() + ".cart";
    private static CartServiceImplement INSTANCE;
    private final ProductService productService;

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
            Cart cart = (Cart) session.getAttribute(SESSION_ATTRIBUTE);

            if (cart == null) {
                cart = new Cart();
                session.setAttribute(SESSION_ATTRIBUTE, cart);
            }

            Product product = productService.getProduct(productId);

            boolean productExistsInCart = cart.getItems().stream()
                    .anyMatch(cartItem -> cartItem.getProduct().getId().equals(productId));

            if (productExistsInCart) {
                cart.getItems().stream()
                        .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                        .findFirst()
                        .ifPresent(existingCartItem -> {
                            int newQuantity = existingCartItem.getQuantity() + quantity;

                            if (product.getStock() < newQuantity) {
                                throw new OutOfStockException(product, newQuantity, product.getStock());
                            }

                            existingCartItem.setQuantity(newQuantity);
                        });
            } else {
                if (product.getStock() < quantity) {
                    throw new OutOfStockException(product, quantity, product.getStock());
                }
                cart.getItems().add(new CartItem(product, quantity));
            }

        } finally {
            sessionLock.unlock();
        }
    }

}
