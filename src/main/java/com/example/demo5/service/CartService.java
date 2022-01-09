package com.example.demo5.service;

import com.example.demo5.dto.AddToCartDto;
import com.example.demo5.dto.CartDto;
import com.example.demo5.dto.CartItemDto;
import com.example.demo5.exceptions.CartItemNotExistException;
import com.example.demo5.models.Cart;
import com.example.demo5.models.Product;
import com.example.demo5.models.User;
import com.example.demo5.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public CartService(){}

    public CartItemDto addToCart(AddToCartDto addToCartDto, Product product, User user){
        Cart cart = new Cart(product, addToCartDto.getQuantity(), user);
        return getDtoFromCart(cartRepository.save(cart));
    }

    public CartDto listCartItems(User user) {
        List<Cart> cartList = cartRepository.findAllByUserOrderByCreatedDateDesc(user);
        List<CartItemDto> cartItems = new ArrayList<>();
        for (Cart cart:cartList){
            CartItemDto cartItemDto = getDtoFromCart(cart);
            cartItems.add(cartItemDto);
        }
        double totalCost = 0;
        for (CartItemDto cartItemDto :cartItems){
            totalCost += (cartItemDto.getProduct().getPrice()* cartItemDto.getQuantity());
        }
        return new CartDto(cartItems,totalCost);
    }

    public static CartItemDto getDtoFromCart(Cart cart) {
        return new CartItemDto(cart);
    }

    public void updateCartItem(AddToCartDto cartDto, User user,Product product){
        Optional<Cart> cart_optional = cartRepository.findById(cartDto.getId());
        Cart cart = cart_optional.get();
        cart.setQuantity(cartDto.getQuantity());
        cart.setCreatedDate(new Date());
        if(cart.getQuantity()==0) {
            deleteCartItem(cartDto.getId(),user.getId());
        }
        else{
            cartRepository.save(cart);
        }
    }

    public void deleteCartItem(int id,int userId) throws CartItemNotExistException {
        if (!cartRepository.existsById(id))
            throw new CartItemNotExistException("Cart id is invalid : " + id);
        cartRepository.deleteById(id);

    }

    public void deleteCartItems(int userId) {
        cartRepository.deleteAll();
    }

    public void deleteUserCartItems(User user) {
        cartRepository.deleteByUser(user);
    }
}


