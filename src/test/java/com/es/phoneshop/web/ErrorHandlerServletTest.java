package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerServletTest {

    private final ErrorHandlerServlet servlet = new ErrorHandlerServlet();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;

    @Before
    public void setUp() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGetShouldForwardToErrorProductNotFoundPageWhenProductNotFoundException() throws ServletException, IOException {
        ProductNotFoundException exception = new ProductNotFoundException(123L);
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(exception);
        when(request.getRequestDispatcher("/WEB-INF/pages/errorProductNotFound.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("productId", 123L);
        verify(requestDispatcher).forward(request, response);
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorProductNotFound.jsp");
    }


    @Test
    public void testDoGetShouldForwardToErrorPageForOtherExceptions() throws ServletException, IOException {
        Exception exception = new Exception("Some error");
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(exception);
        when(request.getRequestDispatcher("/WEB-INF/pages/error.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).getRequestDispatcher("/WEB-INF/pages/error.jsp");
    }


    @Test
    public void testDoGetShouldForwardToErrorPageWhenNoException() throws ServletException, IOException {
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(null);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

}
