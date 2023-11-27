package com.mypill.domain.product.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerTests extends ControllerTest {

    @Test
    @DisplayName("판매자는 상품 등록이 가능하다")
    @WithMockUser(authorities = "SELLER")
    void createProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member("testSeller", Role.SELLER);
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        given(rq.getMember()).willReturn(testSeller);
        given(productService.create(any(ProductRequest.class), any())).willReturn(RsData.successOf(new ProductResponse()));

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
    @DisplayName("판매자는 상품 수정이 가능하다")
    @WithMockUser(authorities = "SELLER")
    void updateProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member("testSeller", Role.SELLER);
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testSeller);
        given(productService.update(any(Member.class), any(Long.class), any(ProductRequest.class))).willReturn(RsData.successOf(new ProductResponse()));

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
    @DisplayName("판매자는 상품 삭제가 가능하다")
    @WithMockUser(authorities = "SELLER")
    void deleteProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member("testSeller", Role.SELLER);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testSeller);
        given(productService.softDelete(any(Member.class), any(Long.class))).willReturn(RsData.successOf(new ProductResponse()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(multipart("/product/delete/{productId}", productId)
                        .with(csrf())
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
        Long productId = 1L;
        given(productService.get(any(Long.class))).willReturn(RsData.successOf(new ProductResponse()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/detail/{productId}", productId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }


}
