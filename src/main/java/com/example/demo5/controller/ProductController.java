package com.example.demo5.controller;

import com.example.demo5.dto.ProductDto;
import com.example.demo5.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductDto> getProducts() {
        return productService.listProducts();
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto addProduct(@RequestBody ProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @PutMapping("/update/{productID}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable("productID") Integer productID, @RequestBody @Valid ProductDto productDto) {
        productService.updateProduct(productID, productDto);
    }
}
