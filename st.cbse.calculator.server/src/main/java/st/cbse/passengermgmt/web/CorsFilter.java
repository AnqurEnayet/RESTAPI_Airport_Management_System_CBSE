package st.cbse.passengermgmt.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A simple CORS filter to allow cross-origin requests from the front-end application.
 * This is crucial for development where the front-end (e.g., running on port 8000)
 * is served from a different origin than the backend REST API (on port 8080).
 */
@WebFilter(urlPatterns = "/*")
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Set the Access-Control-Allow-Origin header to allow requests from any origin.
        // For production, this should be restricted to your specific front-end URL.
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // Set allowed methods
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        
        // Set allowed headers
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization");

        // Handle pre-flight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}