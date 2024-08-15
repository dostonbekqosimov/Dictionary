package com.doston.Dictionary.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static String getSessionUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.warn("Authentication is null");
            return null;
        }
        if (!authentication.isAuthenticated()) {
            logger.warn("Authentication is not authenticated");
            return null;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            logger.warn("Authentication is AnonymousAuthenticationToken");
            return null;
        }
        String username = authentication.getName();
        logger.info("Authenticated username: {}", username);
        return username;
    }
}
