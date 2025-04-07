package com.es.phoneshop.web.servlets;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ProductDaoImplement;
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

    private final static String PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";

    private final static String QUERY_ATTR = "query";
    private final static String SORT_ATTR = "sort";
    private final static String ORDER_ATTR = "order";
    private final static String PRODUCTS_ATTR = "products";

    protected ProductDao productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductDaoImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter(QUERY_ATTR);
        String sortFieldStr = request.getParameter(SORT_ATTR);
        String sortOrderStr = request.getParameter(ORDER_ATTR);

        SortField sortField = StringUtils.isBlank(sortFieldStr)
                ? SortField.NONE
                : SortField.valueOf(sortFieldStr.toUpperCase());

        SortOrder sortOrder = StringUtils.isBlank(sortOrderStr)
                ? SortOrder.NONE
                : SortOrder.valueOf(sortOrderStr.toUpperCase());

        request.setAttribute(PRODUCTS_ATTR, productService.findProducts(query, sortField, sortOrder));
        request.getRequestDispatcher(PRODUCT_LIST_JSP).forward(request, response);
    }

}

