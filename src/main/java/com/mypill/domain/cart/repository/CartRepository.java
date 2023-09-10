package com.mypill.domain.cart.repository;

import com.mypill.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {
    @EntityGraph(attributePaths = {"cartProducts", "cartProducts.product", "cartProducts.product.seller"})
    Optional<Cart> findByMemberId(Long memberId);
}