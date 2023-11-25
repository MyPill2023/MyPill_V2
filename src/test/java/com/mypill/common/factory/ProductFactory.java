package com.mypill.common.factory;

import com.mypill.common.mock.MockProduct;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

public class ProductFactory {
    private ProductFactory(){}

    public static class Builder{}

    public static Product product(String name){
        return createProduct(null, name, null);
    }

    public static Product product(String name, Member seller){
        return createProduct(null, name, seller);
    }

    public static Product product(Long id, String name){
        return createProduct(id, name, null);
    }

    public static Product createProduct(Long id, String name, Member seller){
        return MockProduct.builder()
                .id(id)
                .name(name)
                .description("description")
                .seller(seller)
                .price(100L)
                .stock(100L)
                .build();
    }

    public static ProductRequest mockProductRequest(String name){
        return new ProductRequest(
                name,
                "description",
                100L,
                100L,
                List.of(1L, 2L),
                List.of(1L, 2L),
                new MockMultipartFile("imageFile", new byte[0])
        );
    }

}
