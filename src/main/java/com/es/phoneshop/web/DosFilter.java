package com.es.phoneshop.web;

import com.es.phoneshop.security.DosFilterService;
import com.es.phoneshop.security.impl.DosFilterServiceImplement;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DosFilter implements Filter {

    private DosFilterService dosFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dosFilter = DosFilterServiceImplement.getInstance();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (!dosFilter.isAllowed(servletRequest.getRemoteAddr())) {
            httpServletResponse.sendError(429, "Too Many Requests");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
