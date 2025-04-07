package com.es.phoneshop.web.servlets;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import com.es.phoneshop.utility.InputValidator;
import com.es.phoneshop.utility.UrlPatterns;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {

    private static final String CART_ATTR = "cart";
    private static final String CART_ERRORS_ATTR = "cartErrors";
    private static final String CART_QUANTITIES_ATTR = "cartQuantities";
    private static final String PRODUCT_ID_ATTR = "productId";
    private static final String QUANTITY_ATTR = "quantity";

    private static final String MSG_CART_UPDATE_SUCCESS = "Cart updated successfully";
    private static final String MSG_MSG_INVALID_QUANTITY = "Invalid quantity: ";
    private static final String MSG_OUT_OF_STOCK = "Out of stock: ";

    private static final String CART_JSP = "/WEB-INF/pages/cart.jsp";

    protected CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = CartServiceImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = cartService.getCart(session);

        request.setAttribute(CART_ATTR, cart);
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String[] productIds = request.getParameterValues(PRODUCT_ID_ATTR);
        String[] quantities = request.getParameterValues(QUANTITY_ATTR);

        if (productIds == null) {
            response.sendRedirect(String.format(
                    UrlPatterns.CartPageUrlPattern.CART_PAGE_SUCCESS_URL,
                    request.getContextPath(),
                    MSG_CART_UPDATE_SUCCESS)
            );
            return;
        }

        Map<Long, String> errors = new HashMap<>();
        Map<Long, String> cartQuantities = new HashMap<>();

        for (int i = 0; i < productIds.length; ++i) {
            Long productId = Long.parseLong(productIds[i]);
            String quantityStr = quantities[i].trim();

            if (InputValidator.isInvalidQuantity(quantityStr)) {
                errors.put(productId, MSG_MSG_INVALID_QUANTITY + quantityStr);
                cartQuantities.put(productId, quantityStr);
                continue;
            }

            try {
                int quantity = InputValidator.parseQuantity(quantityStr, request.getLocale());
                cartService.update(session, productId, quantity);
            } catch (ParseException ex) {
                errors.put(productId, MSG_MSG_INVALID_QUANTITY + quantityStr);
                cartQuantities.put(productId, quantityStr);
            } catch (OutOfStockException ex) {
                errors.put(productId, MSG_OUT_OF_STOCK + ex.getStockAvailable());
                cartQuantities.put(productId, quantityStr);
            }
        }

        if (!errors.isEmpty()) {
            session.setAttribute(CART_ERRORS_ATTR, errors);
            session.setAttribute(CART_QUANTITIES_ATTR, cartQuantities);
            response.sendRedirect(String.format(
                    UrlPatterns.CartPageUrlPattern.CART_PAGE,
                    request.getContextPath()
            ));
        } else {
            session.removeAttribute(CART_ERRORS_ATTR);
            session.removeAttribute(CART_QUANTITIES_ATTR);
            response.sendRedirect(String.format(
                    UrlPatterns.CartPageUrlPattern.CART_PAGE_SUCCESS_URL,
                    request.getContextPath(),
                    MSG_CART_UPDATE_SUCCESS)
            );
        }
    }

}
