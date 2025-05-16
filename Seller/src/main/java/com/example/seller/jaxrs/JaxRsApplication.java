package com.example.seller.jaxrs;//package com.jaxrs;
//
//import jakarta.ws.rs.ApplicationPath;
//import jakarta.ws.rs.core.Application;
//import java.util.HashSet;
//import java.util.Set;
//
//@ApplicationPath("/api") // Base path for all REST endpoints
//public class JaxRsApplication extends Application {
//    @Override
//    public Set<Class<?>> getClasses() {
//        Set<Class<?>> classes = new HashSet<>();
//        classes.add(CorsFilter.class); // Register CORS filter
//        classes.add(SellerRestController.class); // Register REST resource
//        return classes;
//    }
//}