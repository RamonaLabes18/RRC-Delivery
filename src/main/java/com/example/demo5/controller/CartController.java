package com.example.demo5.controller;

import com.example.demo5.dto.AddToCartDto;
import com.example.demo5.dto.CartDto;
import com.example.demo5.dto.CartItemDto;
import com.example.demo5.exceptions.AuthenticationFailException;
import com.example.demo5.exceptions.CartItemNotExistException;
import com.example.demo5.exceptions.ProductNotExistException;
import com.example.demo5.models.Product;
import com.example.demo5.models.User;
import com.example.demo5.service.AuthenticationService;
import com.example.demo5.service.CartService;
import com.example.demo5.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/add",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto addToCart(@RequestBody AddToCartDto addToCartDto, @RequestParam("token") String token)
            throws AuthenticationFailException, ProductNotExistException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        Product product = productService.getProductById(addToCartDto.getProductId());
        System.out.println("product to add"+  product.getName());
        return cartService.addToCart(addToCartDto, product, user);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CartDto getCartItems(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        CartDto cartDto = cartService.listCartItems(user);
        return cartDto;
    }

    @PutMapping(value = "/update/{cartItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateCartItem(@RequestBody @Valid AddToCartDto cartDto,
                                                      @RequestParam("token") String token) throws AuthenticationFailException,ProductNotExistException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        Product product = productService.getProductById(cartDto.getProductId());
        cartService.updateCartItem(cartDto, user,product);
    }

    @DeleteMapping("/delete/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCartItem(@PathVariable("cartItemId") int itemID,@RequestParam("token") String token) throws AuthenticationFailException, CartItemNotExistException {
        authenticationService.authenticate(token);
        int userId = authenticationService.getUser(token).getId();
        cartService.deleteCartItem(itemID, userId);
    }

}
