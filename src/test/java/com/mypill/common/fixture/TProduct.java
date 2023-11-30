package com.mypill.common.fixture;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.mock.MockProduct;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.product.entity.Product;

public enum TProduct {

    PRODUCT_1(1L, "product1"),
    PRODUCT_2(2L, "product2"),
    PRODUCT_3(3L, "product3");

    private Long id;
    private String name;

    TProduct(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Product getProduct(){
        return MockProduct.builder()
                .id(this.id)
                .name(this.name)
                .description("description")
                .seller(MemberFactory.member(1L,  "testSeller", Role.SELLER))
                .price(100L)
                .stock(100L)
                .build();
    }
}
