package com.mypill.domain.member.controller;

import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.service.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    void beforeEach(){
        memberService.join(new JoinRequest("testUser", "김철수", "1234", "testUser@test.com", "구매자"));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "BUYER")
    @DisplayName("마이페이지 이동")
    void showMyPageTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myPage"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showMyPage"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "BUYER")
    @DisplayName("내 정보 페이지 이동")
    void showMyInfoTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myInfo"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showMyInfo"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "BUYER")
    @DisplayName("내 게시글 목록 페이지 이동")
    void showMyPostsTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myPosts"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showMyPosts"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "BUYER")
    @DisplayName("내 댓글 목록 페이지 이동")
    void showMyCommentsTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myComments"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showMyComments"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "BUYER")
    @DisplayName("회원 탈퇴")
    void deleteAccountSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/deleteAccount"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("deleteAccount"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "BUYER")
    @DisplayName("이름 변경")
    void nameUpdateSuccessTest() throws Exception {
        // GIVEN
        String newName = "손흥민";

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/name/update")
                        .with(csrf())
                        .param("newName", newName)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("updateName"))
                .andExpect(content().string(Matchers.matchesRegex(".*S-.*")))
        ;
    }
}