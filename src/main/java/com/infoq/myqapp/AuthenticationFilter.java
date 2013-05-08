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
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Token googleAccessToken = (Token) request.getSession().getAttribute(ATTR_GOOGLE_OAUTH_ACCESS_TOKEN);
        Token trelloAccessToken = (Token) request.getSession().getAttribute(ATTR_OAUTH_ACCESS_TOKEN);

        if ("/google-signin.html".equals(request.getServletPath()) && googleAccessToken != null) {
            // already authenticated
            response.sendRedirect("/");

        } else if ("/trello-token.html".equals(request.getServletPath()) && googleAccessToken == null) {
            response.sendRedirect("/google-signin.html");

        } else if ("/favicon.ico".equals(request.getServletPath())
                || "/error-403.html".equals(request.getServletPath())
                || "/google-signin.html".equals(request.getServletPath())
                || "/trello-token.html".equals(request.getServletPath())
                || request.getServletPath().startsWith("/app")
                || request.getServletPath().startsWith("/lib")) {
            // don't care about authentication for these resources
            filterChain.doFilter(servletRequest, servletResponse);

        } else if (!"/google/login".equals(request.getPathInfo())
                && !"/google/callback".equals(request.getPathInfo())
                && googleAccessToken == null) {

            if ("/api".equals(request.getServletPath())) {
                response.sendError(401);
            } else {
                response.sendRedirect("/google-signin.html");
            }

        } else if (!"/trello/login".equals(request.getPathInfo())
                && !"/trello/callback".equals(request.getPathInfo())
                && trelloAccessToken == null
                && googleAccessToken != null) {

            if ("/api".equals(request.getServletPath())) {
                response.sendError(403);
            } else {
                response.sendRedirect("/trello-token.html");
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
