package com.es.phoneshop.web.servlets;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.NullDataException;
import com.es.phoneshop.model.order.Order;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {

    private OrderOverviewPageServlet orderOverviewPageServlet;

    @Mock
    private OrderDao orderDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private Order order;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);

        orderOverviewPageServlet = new OrderOverviewPageServlet();

        ServletConfig config = mock(ServletConfig.class);
        orderOverviewPageServlet.init(config);

        orderOverviewPageServlet.orderDao = orderDao;
    }

    @Test
    public void testDoGetShouldForwardToJspWithOrderWhenSecureIdIsValid() throws ServletException, IOException {
        String secureId = "1234";
        when(request.getPathInfo()).thenReturn("/" + secureId);
        when(orderDao.getOrderBySecureId(secureId)).thenReturn(order);
        when(request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp")).thenReturn(requestDispatcher);

        orderOverviewPageServlet.doGet(request, response);

        verify(orderDao).getOrderBySecureId(secureId);
        verify(request).setAttribute("order", order);
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = NotFoundException.class)
    public void testDoGetShouldThrowNotFoundExceptionWhenOrderNotFound() throws ServletException, IOException {
        String secureId = "nonexistent";
        when(request.getPathInfo()).thenReturn("/" + secureId);
        when(orderDao.getOrderBySecureId(secureId)).thenThrow(new NotFoundException("Order not found"));

        orderOverviewPageServlet.doGet(request, response);
    }

    @Test(expected = NullDataException.class)
    public void testDoGetShouldThrowNullDataExceptionWhenSecureIdIsBlank() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");

        when(orderDao.getOrderBySecureId("")).thenThrow(new NullDataException("Order id cannot be null"));

        orderOverviewPageServlet.doGet(request, response);
    }
}
