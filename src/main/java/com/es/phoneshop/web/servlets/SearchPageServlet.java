package com.es.phoneshop.web.servlets;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ProductDaoImplement;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.search.SearchMode;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.maven.shared.utils.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class SearchPageServlet extends HttpServlet {

    protected ProductDao productService;

    private final static String SEARCH_JSP = "/WEB-INF/pages/advancedSearch.jsp";

    private final static String DESCRIPTION_ATTR = "description";
    private final static String MIN_PRICE_ATTR = "minPrice";
    private final static String MAX_PRICE_ATTR = "maxPrice";

    private final static String SEARCH_MODE_ATTR = "searchMode";

    private final static String ERROR_ATTR = "errors";
    private final static String SUCCESS_ATTR = "success";

    private final static String PRODUCT_ATTR = "products";

    private final static String SUCCESS_MSG = "Product search successfully completed";
    private final static String INVALID_NUMBER_MSG = "Invalid number: ";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductDaoImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String description = request.getParameter(DESCRIPTION_ATTR);
        String minPriceStr = request.getParameter(MIN_PRICE_ATTR);
        String maxPriceStr = request.getParameter(MAX_PRICE_ATTR);
        String searchModeStr = request.getParameter(SEARCH_MODE_ATTR);

        SearchMode searchMode = StringUtils.isBlank(searchModeStr)
                ? SearchMode.NONE
                : SearchMode.valueOf(searchModeStr.toUpperCase());

        Locale locale = request.getLocale();
        Map<String, String> errors = new HashMap<>();

        BigDecimal minPrice = parseBigDecimalWithLocale(minPriceStr, errors, locale, MIN_PRICE_ATTR);
        BigDecimal maxPrice = parseBigDecimalWithLocale(maxPriceStr, errors, locale, MAX_PRICE_ATTR);

        List<Product> products = new ArrayList<>();

        if (errors.isEmpty() && hasSearchAttempted(request)) {
            products = productService.findProductsByParams(description, minPrice, maxPrice, searchMode);
            request.setAttribute(PRODUCT_ATTR, products);
            request.setAttribute(SUCCESS_ATTR, SUCCESS_MSG);
        } else {
            request.setAttribute(ERROR_ATTR, errors);
            request.setAttribute(PRODUCT_ATTR, products);
        }

        request.getRequestDispatcher(SEARCH_JSP).forward(request, response);
    }


    private BigDecimal parseBigDecimalWithLocale(String value,
                                                 Map<String, String> errors,
                                                 Locale locale,
                                                 String attributeName
    ) {

        if (value == null || value.isBlank()) return null;
        if (value.matches(".*[a-zA-Zа-яА-Я].*")) {
            errors.put(attributeName, INVALID_NUMBER_MSG + value);
            return null;
        }

        try {
            NumberFormat format = NumberFormat.getInstance(locale);
            Number number = format.parse(value.trim());
            return new BigDecimal(number.toString());
        } catch (ParseException | NumberFormatException e) {
            errors.put(attributeName, INVALID_NUMBER_MSG + value);
            return null;
        }
    }

    private boolean hasSearchAttempted(HttpServletRequest request) {
        return request.getParameterMap().containsKey(DESCRIPTION_ATTR)
                || request.getParameterMap().containsKey(MIN_PRICE_ATTR)
                || request.getParameterMap().containsKey(MAX_PRICE_ATTR);
    }

}
