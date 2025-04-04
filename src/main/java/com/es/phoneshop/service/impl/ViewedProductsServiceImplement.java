package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ViewedProductsService;
import com.es.phoneshop.utility.SessionLockManager;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class ViewedProductsServiceImplement implements ViewedProductsService {
    private static ViewedProductsServiceImplement instance;
    private static final String SESSION_ATTRIBUTE = ViewedProductsServiceImplement.class.getName() + ".viewedProducts";
    private static final int MAX_VIEWED_PRODUCTS = 3;

    private ViewedProductsServiceImplement() {
    }

    public static synchronized ViewedProductsServiceImplement getInstance() {
        if (instance == null) {
            instance = new ViewedProductsServiceImplement();
        }
        return instance;
    }

    @Override
    public void addViewedProduct(HttpSession session, Product product) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            List<Product> viewedProducts = (List<Product>) session.getAttribute(SESSION_ATTRIBUTE);

            if (viewedProducts == null) {
                viewedProducts = new ArrayList<>();
            }

            viewedProducts = viewedProducts.stream()
                    .filter(p -> !p.getCode().equals(product.getCode()))
                    .limit(MAX_VIEWED_PRODUCTS - 1)
                    .collect(Collectors.toList());

            viewedProducts.add(0, product);

            session.setAttribute(SESSION_ATTRIBUTE, viewedProducts);
        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public List<Product> getLastViewedProducts(HttpSession session) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            return Optional.ofNullable((List<Product>) session.getAttribute(SESSION_ATTRIBUTE))
                    .orElseGet(ArrayList::new);
        } finally {
            sessionLock.unlock();
        }
    }

}
