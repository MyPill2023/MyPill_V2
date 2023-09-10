package com.mypill.domain.cart.repository;

import com.mypill.domain.cart.entity.Cart;

import java.util.Optional;

public interface CartRepositoryCustom {
    Optional<Cart> findCartByMemberId(Long memberId);
}
