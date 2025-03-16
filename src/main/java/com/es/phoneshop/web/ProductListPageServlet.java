package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;

import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.maven.shared.utils.StringUtils;

import java.io.IOException;

public class ProductListPageServlet extends HttpServlet {

    protected ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
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

        request.setAttribute("products", productDao.findProducts(query, sortField, sortOrder));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

}

