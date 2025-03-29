package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.ViewedProductsService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import com.es.phoneshop.service.impl.ProductServiceImplement;
import com.es.phoneshop.service.impl.ViewedProductsServiceImplement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

public class ProductDetailPageServlet extends HttpServlet {

    protected ProductService productService;
    protected CartService cartService;
    protected ViewedProductsService viewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImplement.getInstance();
        cartService = CartServiceImplement.getInstance();
        viewedProductsService = ViewedProductsServiceImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = parseProductId(request);
        Product product = productService.getProduct(productId);

        HttpSession session = request.getSession();
        viewedProductsService.addViewedProduct(session, product);

        request.setAttribute("product", product);
        request.setAttribute("cart", cartService.getCart(session));
        session.setAttribute("viewedProducts", viewedProductsService.getLastViewedProducts(session));
        request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quantityStr = request.getParameter("quantity").trim();
        Long productId = parseProductId(request);
        request.getSession().setAttribute("quantity", quantityStr);

        if (!quantityStr.matches("[\\d.,]+")) {
            response.sendRedirect(request.getContextPath() + "/products/" + parseProductId(request) + "?error=Invalid quantity: " + quantityStr);
            return;
        }

        int quantity;
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            quantity = format.parse(quantityStr).intValue();
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?error=Invalid quantity " + quantityStr);
            return;
        }

        try {
            cartService.add(request.getSession(), productId, quantity);
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?success=Product added to cart");
        } catch (OutOfStockException ex) {
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?error=Out of stock available " + ex.getStockAvailable());
        }
    }

    protected Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo().substring(1);
        return Long.parseLong(productId);
    }

}
