package com.mypill.domain.productlike.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.productlike.entity.ProductLike;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProductLikeControllerTests extends ControllerTest {

    @Test
    @DisplayName("구매자는 상품에 대한 좋아요를 등록할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void likeProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(productLikeService.like(anyLong(), anyLong())).willReturn(RsData.successOf(new ProductLike()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/like/create/{id}", productId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 상품에 대한 좋아요를 취소할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void unlikeProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(productLikeService.unlike(anyLong(), anyLong())).willReturn(RsData.successOf(new ProductLike()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/like/delete/{id}", productId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 관심 상품 목록에서 좋아요를 삭제할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void unlikeFromMyLikesSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Long productId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(productLikeService.unlike(anyLong(), anyLong())).willReturn(RsData.successOf(new ProductLike()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/like/my-list/delete/{id}", productId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }
}
