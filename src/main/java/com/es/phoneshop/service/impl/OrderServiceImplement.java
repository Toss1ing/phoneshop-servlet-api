package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.OrderDaoImplement;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.utility.SessionLockManager;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class OrderServiceImplement implements OrderService {

    protected OrderDao orderServiceDao = OrderDaoImplement.getInstance();
    private static OrderServiceImplement instance;

    public static OrderServiceImplement getInstance() {
        if (instance == null) {
            instance = new OrderServiceImplement();
        }
        return instance;
    }

    @Override
    public Order getOrder(Cart cart, HttpSession session) {
        Lock sessionLock = SessionLockManager.getSessionLock(session);
        sessionLock.lock();

        try {
            Order order = new Order();

            order.setItems(cart.getItems().stream().map(item -> {
                        try {
                            return item.clone();
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList()));

            order.setTotalQuantity(cart.getTotalQuantity());
            order.setSubtotal(cart.getTotalPrice());
            order.setDeliveryCost(calculateDeliveryCost());
            order.setTotalPrice(order.getSubtotal().add(order.getDeliveryCost()));
            return order;
        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public void placeOrder(Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderServiceDao.save(order);
    }

    @Override
    public List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    protected BigDecimal calculateDeliveryCost() {
        return new BigDecimal("100");
    }
}
