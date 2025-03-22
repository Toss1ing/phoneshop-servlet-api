package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.implementation.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ViewedProductsService;
import com.es.phoneshop.model.product.dao.CartDao;
import com.es.phoneshop.model.product.implementation.CartProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
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

    private ProductDao productDao;
    private CartDao cartDao;
    private ViewedProductsService viewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartDao = CartProductDao.getInstance();
        viewedProductsService = new ViewedProductsService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = parseProductId(request);
        Product product = productDao.getProduct(productId);

        HttpSession session = request.getSession();
        viewedProductsService.addViewedProduct(session, product);

        request.setAttribute("product", product);
        request.setAttribute("cart", cartDao.getCart(session));
        request.setAttribute("viewedProducts", viewedProductsService.getLastViewedProducts(session));
        request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String quantityStr = request.getParameter("quantity");
        Long productId = parseProductId(request);

        int quantity;
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            quantity = format.parse(quantityStr).intValue();
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?error=Invalid quantity" + quantityStr);
            return;
        }

        try {
            cartDao.add(request.getSession(), productId, quantity);
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?success=Product added to cart");
        } catch (OutOfStockException ex) {
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?error=Out of stock");
        }

    }


    Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo().substring(1);
        return Long.parseLong(productId);
    }

}
