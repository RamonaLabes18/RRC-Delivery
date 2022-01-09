package com.example.demo5.service;

import com.example.demo5.dto.CartDto;
import com.example.demo5.dto.CartItemDto;
import com.example.demo5.enums.OrderStatus;
import com.example.demo5.exceptions.CartItemNotExistException;
import com.example.demo5.exceptions.CustomException;
import com.example.demo5.exceptions.OrderNotFoundException;
import com.example.demo5.models.Order;
import com.example.demo5.models.OrderItem;
import com.example.demo5.models.User;
import com.example.demo5.repository.OrderItemsRepository;
import com.example.demo5.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemsRepository orderItemsRepository;

    public void placeOrder(User user) {
        // first let get cart items for the user
        CartDto cartDto = cartService.listCartItems(user);
        List<CartItemDto> cartItemDtoList = cartDto.getcartItems();

        if(!(cartDto.getcartItems().isEmpty())) {
            // create the order and save it
            Order newOrder = new Order();
            newOrder.setCreatedDate(new Date());
            newOrder.setUser(user);
            newOrder.setTotalPrice(cartDto.getTotalCost());
            newOrder.setOrderStatus(OrderStatus.CREATED);
            orderRepository.save(newOrder);

            for (CartItemDto cartItemDto : cartItemDtoList) {
                // create orderItem and save each one
                OrderItem orderItem = new OrderItem();
                orderItem.setCreatedDate(new Date());
                orderItem.setPrice(cartItemDto.getProduct().getPrice());
                orderItem.setProduct(cartItemDto.getProduct());
                orderItem.setQuantity(cartItemDto.getQuantity());
                orderItem.setOrder(newOrder);
                // add to order item list
                orderItemsRepository.save(orderItem);
            }
            cartService.deleteUserCartItems(user);
        }
        else{
            throw new CustomException("The cart is empty.");
        }

    }

    public List<Order> listOrders(User user) {
        return orderRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Order getOrder(Integer orderId) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        }
        throw new OrderNotFoundException("Order not found");
    }

    public void changeOrderStatus(int statusNumber,int orderId){
        Order order = orderRepository.getById(orderId);
        if( statusNumber == 1)
        {
            order.setOrderStatus(OrderStatus.CREATED);
        }
        if( statusNumber == 2)
        {
            order.setOrderStatus(OrderStatus.IN_ROGRESS);
        }
        if( statusNumber == 3)
        {
            order.setOrderStatus(OrderStatus.FINISHED);
        }
        if( statusNumber == 3)
        {
            order.setOrderStatus(OrderStatus.CANCELED);
        }

    }
}


