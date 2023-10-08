package com.mypill.domain.cart.repository;

import com.mypill.domain.cart.entity.Cart;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.mypill.domain.cart.entity.QCart.cart;

@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Cart> findCartByMemberId(Long memberId){

        return Optional.ofNullable(jpaQueryFactory.selectFrom(cart)
                .where(cart.member.id.eq(memberId))
                .fetchOne());
    }
}
