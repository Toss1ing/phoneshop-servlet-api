package com.es.phoneshop.web;

import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.ProductServiceImplement;

import com.es.phoneshop.model.product.sort.SortField;
import com.es.phoneshop.model.product.sort.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.maven.shared.utils.StringUtils;

import java.io.IOException;

public class ProductListPageServlet extends HttpServlet {

    protected ProductService productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortFieldStr = request.getParameter("sort");
        String sortOrderStr = request.getParameter("order");

        SortField sortField = StringUtils.isBlank(sortFieldStr)
                ? SortField.NONE
                : SortField.valueOf(sortFieldStr.toUpperCase());

        SortOrder sortOrder = StringUtils.isBlank(sortOrderStr)
                ? SortOrder.NONE
                : SortOrder.valueOf(sortOrderStr.toUpperCase());

        request.setAttribute("products", productService.findProducts(query, sortField, sortOrder));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

}

