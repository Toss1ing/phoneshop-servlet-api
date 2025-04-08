package com.es.phoneshop.dao.impl;

import com.es.phoneshop.exception.ExistException;
import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderDaoImplementTest {

    private OrderDaoImplement orderDao;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        orderDao = OrderDaoImplement.getInstance();
        ResetSingleton();
    }

    private void ResetSingleton() throws IllegalAccessException, NoSuchFieldException {
        java.lang.reflect.Field instanceField = OrderDaoImplement.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    public void testGetOrderBySecureIdSuccess() {
        Order order = new Order();
        order.setSecureId("secureId123");
        orderDao.save(order);

        Order fetchedOrder = orderDao.getOrderBySecureId("secureId123");

        assertNotNull(fetchedOrder);
        assertEquals("secureId123", fetchedOrder.getSecureId());
    }

    @Test(expected = NullDataException.class)
    public void testGetOrderBySecureIdNullSecureId() {
        orderDao.getOrderBySecureId(null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetOrderBySecureIdOrderNotFound() {
        orderDao.getOrderBySecureId("nonExistingSecureId");
    }

    @Test
    public void testSaveOrderSuccess() {
        Order order = new Order();
        order.setSecureId("secureId123");

        orderDao.save(order);

        Order fetchedOrder = orderDao.getOrderBySecureId("secureId123");
        assertNotNull(fetchedOrder);
        assertEquals("secureId123", fetchedOrder.getSecureId());
    }

    @Test(expected = NullDataException.class)
    public void testSaveOrderNullOrder() {
        orderDao.save(null);
    }

    @Test(expected = ExistException.class)
    public void testSaveOrderExistingOrder() {
        Order order = new Order();
        order.setId(123L);
        orderDao.save(order);

        Order duplicateOrder = new Order();
        duplicateOrder.setId(123L);
        orderDao.save(duplicateOrder);
    }
}
