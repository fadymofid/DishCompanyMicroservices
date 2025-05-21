package service2;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import service2.Controller.CORSFilter;
import service2.Controller.DishController;

@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DishController.class);  // your REST resource
        classes.add(CORSFilter.class);      // your CORS filter
        // add other resource or provider classes if needed
        return classes;
    }
}
