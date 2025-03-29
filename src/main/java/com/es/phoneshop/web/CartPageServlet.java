package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CartPageServlet extends HttpServlet {

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

        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");

        if (productIds == null) {
            response.sendRedirect(request.getContextPath() + "/cart?success=Add products to the cart");
            return;
        }

        Map<Long, String> errors = new HashMap<>();
        Map<Long, String> cartQuantities = new HashMap<>();

        for (int i = 0; i < productIds.length; ++i) {
            Long productId = Long.parseLong(productIds[i]);
            String quantityStr = quantities[i].trim();

            if (!quantityStr.matches("[\\d.,]+") || quantityStr.startsWith("0")) {
                errors.put(productId, "Invalid quantity: " + quantityStr);
                cartQuantities.put(productId, quantityStr);
                continue;
            }

            try {
                Locale locale = request.getLocale();
                int quantity = NumberFormat.getNumberInstance(locale).parse(quantityStr).intValue();
                cartService.update(session, productId, quantity);
            } catch (ParseException ex) {
                errors.put(productId, "Invalid quantity: " + quantityStr);
                cartQuantities.put(productId, quantityStr);
            } catch (OutOfStockException ex) {
                errors.put(productId, "Out of stock: " + ex.getStockAvailable());
                cartQuantities.put(productId, quantityStr);
            }
        }

        if (!errors.isEmpty()) {
            session.setAttribute("cartErrors", errors);
            session.setAttribute("cartQuantities", cartQuantities);
            response.sendRedirect(request.getContextPath() + "/cart");
        } else {
            session.removeAttribute("cartErrors");
            session.removeAttribute("cartQuantities");
            response.sendRedirect(request.getContextPath() + "/cart?success=Cart updated successfully");
        }
    }

}
