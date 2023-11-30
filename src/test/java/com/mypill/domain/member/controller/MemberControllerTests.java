package com.mypill.domain.member.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTests extends ControllerTest {

    @Test
    @DisplayName("회원은 마이페이지에 접근할 수 있다")
    @WithMockUser
    void showMyPageSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myPage"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 내 정보 페이지에 접근할 수 있다")
    @WithMockUser
    void showMyInfoTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myInfo"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 내 게시글 목록 페이지에 접근할 수 있다")
    @WithMockUser
    void showMyPostsTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myPosts"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 내 댓글 목록 페이지에 접근할 수 있다")
    @WithMockUser
    void showMyCommentsTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myComments"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 탈퇴할 수 있다")
    @WithMockUser
    void deleteAccountSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember", Role.MEMBER);

        given(rq.getMember()).willReturn(testMember);
        given(memberService.softDelete(any(Member.class))).willReturn(RsData.successOf(testMember));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/deleteAccount"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("회원은 이름 변경이 가능하다")
    @WithMockUser
    void nameUpdateSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember", Role.MEMBER);
        String newName = "newName";

        given(rq.getMember()).willReturn(testMember);
        given(memberService.updateName(any(Member.class), anyString())).willReturn(RsData.successOf(testMember));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/name/update")
                        .with(csrf())
                        .param("newName", newName)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }
}