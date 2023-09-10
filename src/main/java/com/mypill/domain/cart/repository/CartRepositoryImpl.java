package com.mypill.domain.cart.repository;

import com.mypill.domain.cart.dto.response.CartResponse;
import com.mypill.domain.cart.entity.Cart;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.mypill.domain.cart.entity.QCart.cart;
import static com.mypill.domain.cart.entity.QCartProduct.cartProduct;
import static com.mypill.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Cart> findCartByMemberId(Long memberId){

        return Optional.ofNullable(jpaQueryFactory.selectFrom(cart)
                .leftJoin(cart.cartProducts, cartProduct).fetchJoin()
                .leftJoin(cartProduct.product, product).fetchJoin()
                .leftJoin(product.seller).fetchJoin()
                .where(cart.member.id.eq(memberId))
                .fetchOne());
    }
}
