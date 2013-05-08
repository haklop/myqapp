package com.infoq.myqapp;

import org.scribe.model.Token;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    public static final String ATTR_OAUTH_REQUEST_TOKEN = "oauthrequestoken";
    public static final String ATTR_OAUTH_ACCESS_TOKEN = "oauthaccesstoken";

    public static final String ATTR_GOOGLE_OAUTH_ACCESS_TOKEN = "googleoauthaccesstoken";
    public static final String ATTR_GOOGLE_EMAIL = "googleemail";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Token accessToken = (Token) request.getSession().getAttribute(ATTR_GOOGLE_OAUTH_ACCESS_TOKEN);

        if (accessToken == null) {

        }

        if (!"/favicon.ico".equals(request.getServletPath()) && !"/google/login".equals(request.getPathInfo())
                && !"/google/callback".equals(request.getPathInfo()) && accessToken == null) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            if ("/api".equals(request.getServletPath())) {
                response.sendError(401);
            } else {
                response.sendRedirect("/api/google/login");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
        // nothing
    }
}
