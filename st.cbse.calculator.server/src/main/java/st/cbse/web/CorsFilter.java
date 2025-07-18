package st.cbse.web; // This will be your central web utility package

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Single, central CORS (Cross-Origin Resource Sharing) filter for the entire
 * JAX-RS application.
 *
 * This filter adds necessary HTTP headers to allow web browsers to make
 * cross-origin requests to your REST APIs (Passenger, Flight, Baggage, etc.).
 *
 * It is registered automatically by the JAX-RS runtime due to the @Provider annotation.
 */
@Provider // This annotation registers the filter with your JAX-RS application
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // --- IMPORTANT: For production, replace "*" with your actual frontend origin(s) ---
        // Example: "http://localhost:8000", "https://your-frontend-domain.com"
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        // Allowed HTTP methods (GET, POST, PUT, DELETE, OPTIONS, HEAD are standard for REST)
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // Allowed request headers (important if your frontend sends custom headers or auth tokens)
        // Ensure "Authorization" is included if you plan to use token-based authentication (e.g., Bearer tokens)
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                "Origin, Content-Type, Accept, Authorization");

        // Set to "true" if you need to send cookies or HTTP authentication headers across origins.
        // WARNING: If this is "true", "Access-Control-Allow-Origin" CANNOT be "*".
        // It must be a specific origin (e.g., "http://localhost:8000").
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "false"); // Keeping it false for "*" origin

        // Max-Age for pre-flight (OPTIONS) requests. How long the browser can cache the pre-flight response.
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600"); // 1 hour
    }
}