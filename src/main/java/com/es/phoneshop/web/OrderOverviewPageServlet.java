package com.es.phoneshop.web;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.OrderDaoImplement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private final static String ORDER_OVERVIEW_JSP = "/WEB-INF/pages/orderOverview.jsp";

    private final static String ORDER_ATTR = "order";

    protected OrderDao orderServiceDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderServiceDao = OrderDaoImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderSecureId = request.getPathInfo().substring(1);

        request.setAttribute(ORDER_ATTR, orderServiceDao.getOrderBySecureId(orderSecureId));
        request.getRequestDispatcher(ORDER_OVERVIEW_JSP).forward(request, response);

    }

}
