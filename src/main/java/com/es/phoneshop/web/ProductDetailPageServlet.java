package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ProductDaoImplement;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ViewedProductsService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import com.es.phoneshop.service.impl.ViewedProductsServiceImplement;
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

public class ProductDetailPageServlet extends HttpServlet {

    private final static String PRODUCT_JSP = "/WEB-INF/pages/product.jsp";

    private final static String PRODUCT_ATTR = "product";
    private final static String QUANTITY_ATTR = "quantity";
    private final static String CART_ATTR = "cart";
    private final static String OVERVIEW_ATTR = "viewedProducts";

    protected ProductDao productService;
    protected CartService cartService;
    protected ViewedProductsService viewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductDaoImplement.getInstance();
        cartService = CartServiceImplement.getInstance();
        viewedProductsService = ViewedProductsServiceImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = parseProductId(request);
        Product product = productService.getProduct(productId);

        HttpSession session = request.getSession();
        viewedProductsService.addViewedProduct(session, product);

        request.setAttribute(PRODUCT_ATTR, product);
        request.setAttribute(CART_ATTR, cartService.getCart(session));
        session.setAttribute(OVERVIEW_ATTR, viewedProductsService.getLastViewedProducts(session));
        request.getRequestDispatcher(PRODUCT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quantityStr = request.getParameter(QUANTITY_ATTR).trim();
        Long productId = parseProductId(request);
        request.getSession().setAttribute(QUANTITY_ATTR, quantityStr);

        if (InputValidator.isInvalidQuantity(quantityStr)) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductDetailUrlPattern.PRODUCT_DETAIL_ERROR_URL,
                    request.getContextPath(),
                    productId,
                    "Invalid quantity",
                    quantityStr)
            );
            return;
        }

        int quantity;
        try {
            quantity = InputValidator.parseQuantity(quantityStr, request.getLocale());
        } catch (ParseException e) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductDetailUrlPattern.PRODUCT_DETAIL_ERROR_URL,
                    request.getContextPath(),
                    productId,
                    "Invalid quantity",
                    quantityStr)
            );
            return;
        }

        try {
            cartService.add(request.getSession(), productId, quantity);
            response.sendRedirect(String.format(
                    UrlPatterns.ProductDetailUrlPattern.PRODUCT_DETAIL_SUCCESS_URL,
                    request.getContextPath(),
                    productId,
                    "Product added to cart")
            );
        } catch (OutOfStockException ex) {
            response.sendRedirect(String.format(
                    UrlPatterns.ProductDetailUrlPattern.PRODUCT_DETAIL_ERROR_URL,
                    request.getContextPath(),
                    productId,
                    "Out of stock available",
                    ex.getStockAvailable())
            );
        }
    }

    protected Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo().substring(1);
        return Long.parseLong(productId);
    }

}
