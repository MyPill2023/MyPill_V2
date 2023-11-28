package com.mypill.domain.product.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.ProductFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.productlike.entity.ProductLike;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerTests extends ControllerTest {

    @Test
    @DisplayName("판매자는 상품 등록 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void showCreateFormSuccessTest() throws Exception {
        // GIVEN
        given(nutrientService.findAllByOrderByNameAsc()).willReturn(List.of(new Nutrient()));
        given(categoryService.findAllByOrderByNameAsc()).willReturn(List.of(new Category()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자는 상품을 등록할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void createProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member("testSeller", Role.SELLER);
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        given(rq.getMember()).willReturn(testSeller);
        given(productService.create(any(ProductRequest.class), any(Member.class)))
                .willReturn(RsData.successOf(new ProductResponse()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(multipart("/product/create")
                        .file(emptyFile)
                        .with(csrf())
                        .param("sellerId", "3")
                        .param("name", "테스트상품명1")
                        .param("description", "테스트설명1")
                        .param("price", "1000")
                        .param("stock", "10")
                        .param("nutrientIds", "1,2")
                        .param("categoryIds", "1,2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("상품 상세 페이지를 조회할 수 있다")
    @WithMockUser
    void showProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member(1L, "testSeller", Role.SELLER);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testSeller);
        given(rq.isLogin()).willReturn(true);
        given(productService.get(any(Long.class))).willReturn(RsData.successOf(new ProductResponse()));
        given(productLikeService.findByMemberIdAndProductId(anyLong(), anyLong()))
                .willReturn(new ProductLike());

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/detail/{productId}", productId))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("상품 전체 목록 페이지를 조회할 수 있다")
    @WithMockUser
    void showListSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member(1L, "testSeller", Role.SELLER);
        Product product = ProductFactory.product("product", testSeller);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);

        given(nutrientService.findAllByOrderByNameAsc()).willReturn(List.of(new Nutrient()));
        given(categoryService.findAllByOrderByNameAsc()).willReturn(List.of(new Category()));
        given(productService.getAllProductList(any(PageRequest.class)))
                .willReturn(page);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/list/all")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("영양 성분별 상품 목록 페이지를 조회할 수 있다")
    @WithMockUser
    void showListByNutrientSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member(1L, "testSeller", Role.SELLER);
        Product product = ProductFactory.product("product", testSeller);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        Long nutrientId = 1L;

        given(nutrientService.findById(anyLong())).willReturn(Optional.of(new Nutrient()));
        given(nutrientService.findAllByOrderByNameAsc()).willReturn(List.of(new Nutrient()));
        given(categoryService.findAllByOrderByNameAsc()).willReturn(List.of(new Category()));
        given(productService.getAllProductListByNutrientId(anyLong(), any(PageRequest.class)))
                .willReturn(page);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/list/nutrient/{nutrientId}", nutrientId)
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("주요 기능별 상품 목록 페이지를 조회할 수 있다")
    @WithMockUser
    void showListByCategorySuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member(1L, "testSeller", Role.SELLER);
        Product product = ProductFactory.product("product", testSeller);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        Long categoryId = 1L;

        given(nutrientService.findAllByOrderByNameAsc()).willReturn(List.of(new Nutrient()));
        given(categoryService.findAllByOrderByNameAsc()).willReturn(List.of(new Category()));
        given(categoryService.findById(anyLong())).willReturn(Optional.of(new Category()));
        given(productService.getAllProductListByCategoryId(anyLong(), any(PageRequest.class)))
                .willReturn(page);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/list/category/{categoryId}", categoryId)
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자는 상품 수정 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void showUpdateFormSuccessTest() throws Exception {
        // GIVEN
        Long productId = 1L;

        given(nutrientService.findAllByOrderByNameAsc()).willReturn(List.of(new Nutrient()));
        given(categoryService.findAllByOrderByNameAsc()).willReturn(List.of(new Category()));
        given(productService.get(anyLong())).willReturn(RsData.successOf(new ProductResponse()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/update/{productId}", productId))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자는 자신의 상품을 수정할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void updateProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member("testSeller", Role.SELLER);
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testSeller);
        given(productService.update(any(Member.class), anyLong(), any(ProductRequest.class)))
                .willReturn(RsData.successOf(new ProductResponse()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(multipart("/product/update/{productId}", productId)
                        .file(emptyFile)
                        .with(csrf())
                        .param("sellerId", String.valueOf(testSeller.getId()))
                        .param("name", "수정상품명")
                        .param("description", "수정설명")
                        .param("price", "1000")
                        .param("stock", "10")
                        .param("nutrientIds", "1,2")
                        .param("categoryIds", "1,2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자는 자신의 상품을 삭제할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void deleteProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member("testSeller", Role.SELLER);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testSeller);
        given(productService.softDelete(any(Member.class), anyLong()))
                .willReturn(RsData.successOf(new ProductResponse()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/delete/{productId}", productId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

}
