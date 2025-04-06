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

    private static final String MSG_PRODUCT_ADD_TO_CART = "Product added to cart";
    private static final String MSG_INVALID_QUANTITY = "Invalid quantity: ";
    private static final String MSG_OUT_OF_STOCK = "Out of stock: ";

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
                    MSG_INVALID_QUANTITY,
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
                    MSG_INVALID_QUANTITY,
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
                    MSG_PRODUCT_ADD_TO_CART)
            );
        } catch (OutOfStockException ex) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductListAddCartItemUrlPattern.PRODUCT_LIST_ADD_CART_ITEM_ERROR_URL,
                    request.getContextPath(),
                    MSG_OUT_OF_STOCK,
                    ex.getStockAvailable(),
                    productId,
                    quantityStr)
            );
        }

    }

}
