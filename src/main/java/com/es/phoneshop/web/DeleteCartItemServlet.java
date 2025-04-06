package com.es.phoneshop.web;

import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import com.es.phoneshop.utility.UrlPatterns;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {

    private static final String CART_ERRORS_ATTR = "cartErrors";

    private static final String MSG_REMOVE_PRODUCT = "Product removed";

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

        if (session.getAttribute(CART_ERRORS_ATTR) != null) {
            session.removeAttribute(CART_ERRORS_ATTR);
        }

        response.sendRedirect(String.format(
                UrlPatterns.DeleteCartItemUrlPattern.DELETE_CART_ITEM_SUCCESS_URL,
                request.getContextPath(),
                MSG_REMOVE_PRODUCT)
        );
    }

    protected Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo().substring(1);
        return Long.parseLong(productId);
    }

}
