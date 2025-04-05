package com.es.phoneshop.security;

import jakarta.servlet.http.HttpServletRequest;

public interface DosFilterService {
    boolean isAllowed(String ipAddress);
}
