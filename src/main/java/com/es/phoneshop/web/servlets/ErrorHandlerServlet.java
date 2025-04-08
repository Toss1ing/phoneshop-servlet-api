package com.es.phoneshop.web.servlets;

import com.es.phoneshop.exception.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorHandlerServlet extends HttpServlet {

    private static final String EXCEPTION_ATTR = "jakarta.servlet.error.exception";
    private static final String REQUEST_CODE_ATTR = "jakarta.servlet.error.status_code";

    private static final String TO_MANY_REQUEST_JSP = "/WEB-INF/pages/errorToManyRequests.jsp";
    private static final String ERROR_ENTITY_NOT_FOUND_JSP = "/WEB-INF/pages/errorEntityNotFound.jsp";
    private static final String ERROR_JSP = "/WEB-INF/pages/error.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Throwable throwable = (Throwable) request.getAttribute(EXCEPTION_ATTR);
        Integer statusCode = (Integer) request.getAttribute(REQUEST_CODE_ATTR);

        if (statusCode != null && statusCode == 429) {
            request.getRequestDispatcher(TO_MANY_REQUEST_JSP).forward(request, response);
            return;
        }
        if (throwable instanceof NotFoundException) {
            request.getRequestDispatcher(ERROR_ENTITY_NOT_FOUND_JSP).forward(request, response);
        } else {
            request.getRequestDispatcher(ERROR_JSP).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
