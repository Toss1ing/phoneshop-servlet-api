package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.GenericDao;
import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.model.order.Order;
import org.eclipse.jetty.util.StringUtil;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OrderDaoImplement extends GenericDao<Order> implements OrderDao {

    private static OrderDaoImplement INSTANCE;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    private OrderDaoImplement() {
        entities = new ArrayList<>();
        countId = 0L;
    }

    public static synchronized OrderDaoImplement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderDaoImplement();
        }
        return INSTANCE;
    }

    @Override
    protected Long getId(Order order) {
        return order.getId();
    }

    @Override
    protected void setId(Order order) {
        order.setId(countId++);
    }

    @Override
    public Order getOrderBySecureId(String secureId) {
        if (StringUtil.isBlank(secureId)) {
            throw new NullDataException("Order id cannot be null");
        }

        reentrantReadWriteLock.readLock().lock();

        try {
            return entities.stream()
                    .filter(entity -> entity.getSecureId().equals(secureId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Order not found"));
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }
}
