package com.example.seller.jaxrs;


import com.example.seller.ejb.SellerService;
import com.example.seller.models.Dish;
import com.example.seller.models.OrderItem;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/seller")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SellerRestController {

    @EJB
    private SellerService sellerService;

    @POST
    @Path("/order-items")
    public Response addOrderItem(OrderItem item) {
        sellerService.addOrderItem(item);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/order-items/{orderId}")
    public Response getOrderItems(@PathParam("orderId") Long orderId) {
        List<OrderItem> items = sellerService.getOrderItemsByOrderId(orderId);
        return Response.ok(items).build();
    }
    @POST
    @Path("/dishes")
    public Response createDish(@QueryParam("sellerId") Long sellerId, Dish dish) {
        try {
            // Validate input
            if (sellerId == null || dish == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Seller ID and dish data are required")
                        .build();
            }

            // Business logic
            sellerService.createDish(dish, sellerId);

            // Return success response without CORS headers (let global filter handle them)
            return Response.status(Response.Status.CREATED)
                    .entity(dish) // Return created dish with ID
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create dish: " + e.getMessage())
                    .build();
        }
    }
//    @OPTIONS
//    @Path("/dishes")
//    public Response optionsForDishes() {
//        return Response.ok()
//                .header("Access-Control-Allow-Origin", "http://localhost:63342")
//                .header("Access-Control-Allow-Methods", "POST, OPTIONS")
//                .header("Access-Control-Allow-Headers", "Content-Type")
//                .build();
//    }
}
