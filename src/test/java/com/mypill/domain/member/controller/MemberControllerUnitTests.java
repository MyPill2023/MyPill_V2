package com.mypill.domain.member.controller;

import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class MemberControllerUnitTests {

    private MockMvc mvc;
    @InjectMocks
    private MemberController memberController;
    @Mock
    private MemberService memberService;
    @Mock
    private Rq rq;

    @BeforeEach
    void beforeEach(){
        mvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    @DisplayName("비로그인 상태에서 로그인 페이지 접근이 가능하다")
    @WithAnonymousUser
    void showLoginSuccessUnitTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/login"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showLogin"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("비로그인 상태에서 회원가입 페이지 접근이 가능하다")
    @WithAnonymousUser
    void showJoinSuccessUnitTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showJoin"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("비로그인 상태에 회원가입을 할 수 있다")
    @WithAnonymousUser
    void joinSuccessUnitTest() throws Exception {
        // GIVEN
        given(memberService.join(any(JoinRequest.class))).willReturn(RsData.successOf(new Member()));
        given(rq.redirectWithMsg(any(String.class), any(String.class))).willCallRealMethod();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username", "testUser1")
                        .param("name", "김철수")
                        .param("password", "abc12345")
                        .param("email", "testUser@test.com")
                        .param("userType", "구매자")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @DisplayName("회원가입 시 아이디 중복확인을 할 수 있다")
    void usernameCheckUnitTest() throws Exception {
        // GIVEN
        String username = "testUser1";

        given(memberService.usernameValidation(any(String.class))).willReturn(UsernameValidationResult.VALIDATION_OK);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join/usernameCheck")
                        .with(csrf())
                        .param("username", username)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("usernameCheck"))
                .andExpect(content().json("{\"resultCode\":\"" + UsernameValidationResult.VALIDATION_OK.getResultCode()
                        + "\",\"msg\":\"" + UsernameValidationResult.VALIDATION_OK.getMessage()
                        + "\",\"data\":\"VALIDATION_OK\"}"))
        ;
    }

    @Test
    @DisplayName("회원가입 시 이메일 중복확인을 할 수 있다")
    void emailCheckUnitTest() throws Exception {
        // GIVEN
        String email = "testEmail@test.com";

        given(memberService.emailValidation(any(String.class))).willReturn(EmailValidationResult.VALIDATION_OK);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join/emailCheck")
                        .with(csrf())
                        .param("email", email)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("emailCheck"))
                .andExpect(content().json("{\"resultCode\":\"" + EmailValidationResult.VALIDATION_OK.getResultCode()
                        + "\",\"msg\":\"" + EmailValidationResult.VALIDATION_OK.getMessage()
                        + "\",\"data\":\"VALIDATION_OK\"}"))
        ;
    }
}