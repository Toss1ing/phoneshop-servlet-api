package com.es.phoneshop.web.servlets;

import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MiniCartServlet extends HttpServlet {

    private final static String MINI_CART_JSP = "/WEB-INF/pages/miniCart.jsp";
    private final static String CART_ATTR = "cart";

    protected CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = CartServiceImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(CART_ATTR, cartService.getCart(request.getSession()));
        request.getRequestDispatcher(MINI_CART_JSP).include(request, response);
    }

}
