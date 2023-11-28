package com.mypill.domain.notification.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.NotificationFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationControllerTests extends ControllerTest {

    @Test
    @DisplayName("회원은 알림 목록 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void showListSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember", Role.MEMBER);
        Notification notification = NotificationFactory.orderPayment(testMember);

        given(rq.getMember()).willReturn(testMember);
        given(notificationService.findByMemberId(anyLong())).willReturn(List.of(notification));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/notification/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 알림 읽음 처리를 할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void readSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember", Role.MEMBER);
        Notification notification = NotificationFactory.orderPayment(testMember);
        Long notificationId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(notificationService.markAsRead(any(Member.class), anyLong())).willReturn(RsData.successOf(notification));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/notification/read/{notificationId}", notificationId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

}
