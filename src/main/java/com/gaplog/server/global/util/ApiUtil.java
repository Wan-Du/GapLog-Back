package com.gaplog.server.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class ApiUtil {
    public static Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Long.valueOf(userDetails.getUsername());
        } else {
            throw new IllegalStateException("Authentication information is missing or invalid.");
        }
    }
}
