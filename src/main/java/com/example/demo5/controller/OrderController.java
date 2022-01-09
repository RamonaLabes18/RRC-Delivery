package com.example.demo5.controller;

import com.example.demo5.exceptions.AuthenticationFailException;
import com.example.demo5.exceptions.OrderNotFoundException;
import com.example.demo5.models.Order;
import com.example.demo5.models.User;
import com.example.demo5.service.AuthenticationService;
import com.example.demo5.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void placeOrder(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        orderService.placeOrder(user);
    }

    @GetMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> getAllOrders(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        return orderService.listOrders(user);
    }

    // get orderitems for an order
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable("id") Integer id, @RequestParam("token") String token)
            throws AuthenticationFailException {
        authenticationService.authenticate(token);
        try {
            Order order = orderService.getOrder(id);
            return new ResponseEntity<>(order,HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/updateStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateStatus(@RequestParam("status") int statusNumber,@RequestParam("orderId") int orderId) throws AuthenticationFailException {
        orderService.changeOrderStatus(statusNumber,orderId);
    }

}
