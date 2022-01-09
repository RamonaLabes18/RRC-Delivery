package com.example.demo5.service;

import com.example.demo5.dto.ProductDto;
import com.example.demo5.exceptions.ProductNotExistException;
import com.example.demo5.models.Product;
import com.example.demo5.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductDto> listProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();
        for(Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    public static ProductDto getDtoFromProduct(Product product) {
        ProductDto productDto = new ProductDto(product);
        return productDto;
    }

    public static Product getProductFromDto(ProductDto productDto) {
        Product product = new Product(productDto);
        return product;
    }

    public ProductDto addProduct(ProductDto productDto) {
        Product product = getProductFromDto(productDto);
        return getDtoFromProduct(productRepository.save(product));
    }

    public void updateProduct(Integer productID, ProductDto productDto) {
        Product product = getProductFromDto(productDto);
        product.setId(productID);
        productRepository.save(product);
    }


    public Product getProductById(Integer productId) throws ProductNotExistException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (!optionalProduct.isPresent())
            throw new ProductNotExistException("Product id is invalid " + productId);
        return optionalProduct.get();
    }


}
