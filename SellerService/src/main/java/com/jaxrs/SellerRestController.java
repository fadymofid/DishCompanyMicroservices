package com.jaxrs;


import com.ejb.SellerService;
import com.models.OrderItem;
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
}
