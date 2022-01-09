package com.example.demo5.models;

import com.example.demo5.dto.ProductDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private @NotNull String name;
    private @NotNull double price;
    private @NotNull String description;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private List<Cart> carts;


    public Product(ProductDto productDto) {
        this.name = productDto.getName();
        this.description = productDto.getDescription();
        this.price = productDto.getPrice();
    }

    public Product(String name, double price, String description) {
        super();
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product() {
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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
