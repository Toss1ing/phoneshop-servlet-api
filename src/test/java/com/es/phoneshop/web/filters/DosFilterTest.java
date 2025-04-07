package com.es.phoneshop.web.filters;

import com.es.phoneshop.security.DosFilterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class DosFilterTest {

    private DosFilter dosFilter;

    @Mock
    private DosFilterService dosFilterService;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Mock
    private FilterChain filterChain;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dosFilter = new DosFilter();
        dosFilter.dosFilterService = dosFilterService;
    }

    @Test
    public void testRequestAllowed() throws IOException, ServletException {
        String ipAddress = "192.168.0.1";

        when(dosFilterService.isAllowed(ipAddress)).thenReturn(true);
        when(servletRequest.getRemoteAddr()).thenReturn(ipAddress);

        dosFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain, times(1)).doFilter(servletRequest, servletResponse);
        verify(servletResponse, never()).sendError(429);
    }

    @Test
    public void testRequestBlocked() throws IOException, ServletException {
        String ipAddress = "192.168.0.1";

        when(dosFilterService.isAllowed(ipAddress)).thenReturn(false);
        when(servletRequest.getRemoteAddr()).thenReturn(ipAddress);

        dosFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(servletResponse, times(1)).sendError(429, "Too Many Requests");
        verify(filterChain, never()).doFilter(servletRequest, servletResponse);
    }

}