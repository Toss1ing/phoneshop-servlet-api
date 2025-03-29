package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

public class ProductListAddCartItemServlet extends HttpServlet {

    protected CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.cartService = CartServiceImplement.getInstance();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quantityStr = request.getParameter("quantity").trim();
        Long productId = Long.parseLong(request.getParameter("productId").trim());

        if (!quantityStr.matches("[\\d.,]+") || quantityStr.startsWith("0")) {
            response.sendRedirect(request.getContextPath() + "/products" +
                    "?error=Invalid quantity " + quantityStr + "&productId=" + productId + "&quantity=" + quantityStr);
            return;
        }

        int quantity;
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            quantity = format.parse(quantityStr).intValue();
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() + "/products" +
                    "?error=Invalid quantity&productId=" + productId + "&quantity=" + quantityStr);
            return;
        }

        try {
            cartService.add(request.getSession(), productId, quantity);
            response.sendRedirect(request.getContextPath() + "/products?success=Product added to cart");
        } catch (OutOfStockException ex) {
            response.sendRedirect(request.getContextPath() + "/products" +
                    "?error=Out of stock available " + ex.getStockAvailable() +
                    "&productId=" + productId + "&quantity=" + quantityStr);
        }

    }

}
