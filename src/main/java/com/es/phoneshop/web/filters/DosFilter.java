package com.es.phoneshop.web.filters;

import com.es.phoneshop.security.DosFilterService;
import com.es.phoneshop.security.impl.DosFilterServiceImplement;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DosFilter implements Filter {

    protected DosFilterService dosFilterService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dosFilterService = DosFilterServiceImplement.getInstance();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (!dosFilterService.isAllowed(servletRequest.getRemoteAddr())) {
            httpServletResponse.sendError(429, "Too Many Requests");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
