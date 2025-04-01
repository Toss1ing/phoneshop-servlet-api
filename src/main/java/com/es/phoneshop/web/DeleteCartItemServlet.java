package com.es.phoneshop.web;

import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {

    protected CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.cartService = CartServiceImplement.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = parseProductId(request);
        HttpSession session = request.getSession();

        cartService.delete(session, productId);

        if (session.getAttribute("cartErrors") != null) {
            session.removeAttribute("cartErrors");
        }

        response.sendRedirect(request.getContextPath() + "/cart?success=Product removed");
    }

    protected Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo().substring(1);
        return Long.parseLong(productId);
    }

}
