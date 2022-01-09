package com.example.demo5.repository;

import com.example.demo5.models.Cart;
import com.example.demo5.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findAllByUserOrderByCreatedDateDesc(User user);
    Cart findById(Long id);
    List<Cart> deleteByUser(User user);

}
