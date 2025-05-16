package com.example.seller.jaxrs;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        // Allow your frontend origin
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "http://localhost:63342"
        );
        // Allow specific HTTP methods
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT"
        );
        // Allow headers like Content-Type
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers", "Content-Type"
        );
        // Cache preflight response for 24 hours
        responseContext.getHeaders().add(
                "Access-Control-Max-Age", "86400"
        );
    }
}