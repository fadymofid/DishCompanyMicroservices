package service2.Controller;

import java.util.List;
import java.util.Map;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import service2.DTO.DishStockRequest;
import service2.Model.Dish;
import service2.Services.DishService;

@Path("/dishes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DishController {

    @EJB
    private DishService dishService;

    // CREATE
    @POST
    public void addDish(Dish dish) throws Exception {
        dishService.addDish(dish);
    }

    // READ

    /** Get one dish by its ID */
    @GET
    @Path("/{id}")
    public Dish getDishById(@PathParam("id") int id) throws Exception {
        return dishService.getDishById(id);
    }

    /** Get all dishes in the system */
    @GET
    public List<Dish> getAllDishes() throws Exception {
        return dishService.getAllDishes();
    }

    /** Get all dishes for a specific seller */
    @GET
    @Path("/seller/{sellerId}")
    public List<Dish> getDishesBySeller(@PathParam("sellerId") int sellerId) throws Exception {
        return dishService.getAllDishesBySeller(sellerId);
    }

    /** Get all dishes bought by a specific customer */
    @GET
    @Path("/customer/{customerId}")
    public List<Dish> getDishesByCustomer(@PathParam("customerId") int customerId) throws Exception {
        return dishService.getDishesByCustomerId(customerId);
    }

    // UPDATE

    @PUT
    public void updateDish(Dish dish) throws Exception {
        dishService.updateDish(dish);
    }

    /** Decrement stock after an order */
    @POST
    @Path("/deduct-stock")
    public void deductStock(DishStockRequest req) throws Exception {
        dishService.deductStock(req.getDishId(), req.getQuantity());
    }

    // DELETE

    @DELETE
    @Path("/{id}")
    public void deleteDish(@PathParam("id") int id) throws Exception {
        dishService.deleteDish(id);
    }

@POST
@Path("/sold-dishes")
public void addSoldDish(Map<String, Integer> data) throws Exception {
    dishService.addSoldDish(data.get("dishId"), data.get("customerId"));
}
}
