package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import com.es.phoneshop.utility.InputValidator;
import com.es.phoneshop.utility.UrlPatterns;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;

public class ProductListAddCartItemServlet extends HttpServlet {

    protected CartService cartService;

    private static final String QUANTITY_ATTR = "quantity";
    private static final String PRODUCT_ID_ATTR = "productId";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.cartService = CartServiceImplement.getInstance();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quantityStr = request.getParameter(QUANTITY_ATTR).trim();
        Long productId = Long.parseLong(request.getParameter(PRODUCT_ID_ATTR).trim());

        if (InputValidator.isInvalidQuantity(quantityStr)) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductListAddCartItemUrlPattern.PRODUCT_LIST_ADD_CART_ITEM_ERROR_URL,
                    request.getContextPath(),
                    "Invalid quantity ",
                    quantityStr,
                    productId,
                    quantityStr)
            );
            return;
        }

        int quantity;
        try {
            quantity = InputValidator.parseQuantity(quantityStr, request.getLocale());
        } catch (ParseException e) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductListAddCartItemUrlPattern.PRODUCT_LIST_ADD_CART_ITEM_ERROR_URL,
                    request.getContextPath(),
                    "Invalid quantity ",
                    quantityStr,
                    productId,
                    quantityStr)
            );
            return;
        }

        try {
            cartService.add(request.getSession(), productId, quantity);
            response.sendRedirect(String.format(
                    UrlPatterns.ProductListAddCartItemUrlPattern.PRODUCT_LIST_CART_ITEM_SUCCESS_URL,
                    request.getContextPath(),
                    "Product added to cart")
            );
        } catch (OutOfStockException ex) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductListAddCartItemUrlPattern.PRODUCT_LIST_ADD_CART_ITEM_ERROR_URL,
                    request.getContextPath(),
                    "Out of stock available ",
                    ex.getStockAvailable(),
                    productId,
                    quantityStr)
            );
        }

    }

}
