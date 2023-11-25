package com.mypill.common.factory;

import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class CartFactory {

    private CartFactory() {}

    public static Cart cart(Member member){
        return createCart(member, new ArrayList<>());
    }

    public static Cart cart(Member member, List<CartProduct> cartProducts){
        return createCart(member, cartProducts);
    }

    public static CartProduct cartProduct(){
        return createCartProduct(null, null);
    }

    public static CartProduct cartProduct(Product product){
        return createCartProduct(null, product);
    }

    public static CartProduct cartProduct(Cart cart, Product product){
        return createCartProduct(cart, product);
    }

    public static Cart createCart(Member member, List<CartProduct> cartProducts) {
        return Cart.builder()
                .member(member)
                .cartProducts(cartProducts)
                .build();
    }

    public static CartProduct createCartProduct(Cart cart, Product product) {
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(1L)
                .build();
    }

    public static Cart addCartProduct(Cart cart, CartProduct cartProduct) {
        List<CartProduct> cartProducts = cart.getCartProducts();
        cartProducts.add(cartProduct);
        return cart.toBuilder().cartProducts(cartProducts).build();
    }
}
