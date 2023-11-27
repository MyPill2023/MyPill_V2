package com.mypill.domain.cart.controller;

import com.mypill.common.factory.CartFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartControllerTests extends ControllerTest {

    @Test
    @DisplayName("구매자는 장바구니를 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showCartProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);

        given(rq.getMember()).willReturn(testBuyer);
        given(cartService.getOrCreateCart(any(Member.class))).willReturn(CartFactory.cart(testBuyer));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/cart"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 장바구니에 상품을 추가할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void addCartProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);

        given(rq.getMember()).willReturn(testBuyer);
        given(cartService.addCartProduct(any(Member.class), any(CartProductRequest.class))).willReturn(RsData.successOf(new CartProduct()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/cart/add")
                        .with(csrf())
                        .param("productId", "1")
                        .param("quantity", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 장바구니에 담긴 상품의 수량을 업데이트할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void updateQuantitySuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);

        given(rq.getMember()).willReturn(testBuyer);
        given(cartService.updateCartProductQuantity(any(Member.class), anyLong(), anyLong()))
                .willReturn(RsData.successOf(new CartProduct()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/cart/update")
                        .with(csrf())
                        .param("cartProductId", "1")
                        .param("newQuantity", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 장바구니에 담긴 상품을 삭제할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void deleteCartProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);

        given(rq.getMember()).willReturn(testBuyer);
        given(cartService.hardDeleteCartProduct(any(Member.class), anyLong()))
                .willReturn(RsData.successOf(new CartProduct()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/cart/delete")
                        .with(csrf())
                        .param("cartProductId", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 장바구니에 담긴 상품을 전체 삭제할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void deleteAllCartProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);

        given(rq.getMember()).willReturn(testBuyer);
        given(cartService.hardDeleteAllCartProduct(any(Member.class)))
                .willReturn(RsData.successOf(CartFactory.cart(testBuyer)));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/cart/delete/all"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }
}
