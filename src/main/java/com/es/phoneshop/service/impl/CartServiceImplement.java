package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ProductDaoImplement;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.utility.SessionLockManager;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

public class CartServiceImplement implements CartService {

    private static final String SESSION_ATTRIBUTE = CartServiceImplement.class.getName() + ".cart";
    private static CartServiceImplement INSTANCE;
    protected ProductDao productService;

    public static CartServiceImplement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CartServiceImplement();
        }
        return INSTANCE;
    }

    private CartServiceImplement() {
        productService = ProductDaoImplement.getInstance();
    }

    @Override
    public Cart getCart(HttpSession session) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            Cart cart = (Cart) session.getAttribute(SESSION_ATTRIBUTE);

            if (cart == null) {
                session.setAttribute(SESSION_ATTRIBUTE, cart = new Cart());
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
            Cart cart = getCart(session);

            Product product = productService.getProduct(productId);

            CartItem cartItem = findCartItemByProduct(cart, product);

            if (cartItem != null) {
                int newQuantity = cartItem.getQuantity() + quantity;
                validateStock(product, newQuantity);
                cartItem.setQuantity(newQuantity);
            } else {
                validateStock(product, quantity);
                cart.getItems().add(new CartItem(product, quantity));
            }

            recalculateCart(cart);

        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public void update(HttpSession session, Long productId, int quantity) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            Cart cart = getCart(session);

            Product product = productService.getProduct(productId);

            CartItem cartItem = findCartItemByProduct(cart, product);

            validateStock(product, quantity);

            cartItem.setQuantity(quantity);

            recalculateCart(cart);

        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public void delete(HttpSession session, Long productId) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            Cart cart = getCart(session);

            cart.getItems().removeIf(
                    cartItem -> productId.equals(cartItem.getProduct().getId())
            );

            recalculateCart(cart);

        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public void clear(HttpSession session) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            Cart cart = getCart(session);

            cart.getItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setTotalQuantity(0);
        } finally {
            sessionLock.unlock();
        }
    }

    private CartItem findCartItemByProduct(Cart cart, Product product) {
        return cart.getItems().stream()
                .filter(item -> product.getId().equals(item.getProduct().getId()))
                .findFirst()
                .orElse(null);
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getStock());
        }
    }

    protected void recalculateCart(Cart cart) {
        cart.setTotalPrice(BigDecimal.valueOf(cart.getItems().stream()
                .mapToLong(item -> (long) item.getQuantity() * item.getProduct().getPrice().longValue())
                .sum()));
        cart.setTotalQuantity(cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum());
    }

}
