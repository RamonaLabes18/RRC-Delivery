package com.example.demo5.dto;

import com.example.demo5.models.Product;
import javax.validation.constraints.NotNull;

public class ProductDto {

    private Integer id;
    private @NotNull String name;
    private @NotNull double price;
    private @NotNull String description;

    public ProductDto(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setDescription(product.getDescription());
        this.setPrice(product.getPrice());
    }

    public ProductDto(@NotNull String name, @NotNull double price, @NotNull String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public ProductDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

