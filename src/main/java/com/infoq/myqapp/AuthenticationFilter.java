package com.infoq.myqapp;

import org.scribe.model.Token;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    public static final String ATTR_OAUTH_REQUEST_TOKEN = "oauthrequestoken";
    public static final String ATTR_OAUTH_ACCESS_TOKEN = "oauthaccesstoken";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Token requestToken = (Token) request.getSession().getAttribute(ATTR_OAUTH_REQUEST_TOKEN);
        Token accessToken = (Token) request.getSession().getAttribute(ATTR_OAUTH_ACCESS_TOKEN);

        if (!"/trello/login".equals(request.getPathInfo()) && !"/trello/callback".equals(request.getPathInfo()) &&
                (requestToken == null || accessToken == null)) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendRedirect("/api/trello/login");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
        // nothing
    }
}
