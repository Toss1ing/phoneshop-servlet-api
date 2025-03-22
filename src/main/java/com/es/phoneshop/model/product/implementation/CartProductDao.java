package com.es.phoneshop.model.product.implementation;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SessionLockManager;
import com.es.phoneshop.model.product.cart.Cart;
import com.es.phoneshop.model.product.cart.CartItem;
import com.es.phoneshop.model.product.dao.CartDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.http.HttpSession;

import java.util.concurrent.locks.Lock;

public class CartProductDao implements CartDao {

    private static final String SESSION_ATTRIBUTE = CartProductDao.class.getName() + ".cart";
    private static CartProductDao INSTANCE;
    private final ProductDao productDao;

    public static CartProductDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CartProductDao();
        }
        return INSTANCE;
    }

    private CartProductDao() {
        productDao = ArrayListProductDao.getInstance();
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

            Product product = productDao.getProduct(productId);

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
