package com.mypill.domain.diary.controller;

import com.mypill.common.factory.DiaryFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.diary.dto.request.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DiaryControllerTests extends ControllerTest {

    private Member testBuyer;
    private Diary diary;

    @BeforeEach
    void setUp(){
        testBuyer = MemberFactory.member("testBuyer");
        diary = DiaryFactory.diary(testBuyer);

        given(rq.getMember()).willReturn(testBuyer);
    }

    @Test
    @DisplayName("구매자 회원은 복용 스케줄 등록 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showCreateFormSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/diary/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 복용 스케줄을 등록할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void createSuccessTest() throws Exception {
        // GIVEN
        given(diaryService.create(any(DiaryRequest.class), any(Member.class)))
                .willReturn(RsData.successOf(diary));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/diary/create")
                        .with(csrf())
                        .param("name", "name")
                        .param("time", "09:00")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 복용 스케줄 목록을 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showListSuccessTest() throws Exception {
        // GIVEN
        given(diaryService.getList(anyLong())).willReturn(List.of(diary));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/diary/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 복용 스케줄을 삭제할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void deleteSuccessTest() throws Exception {
        // GIVEN
        Long diaryId = 1L;
        given(diaryService.delete(anyLong(), any(Member.class)))
                .willReturn(RsData.successOf(diary));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/diary/delete/{diaryId}", diaryId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 복용 스케줄 기록 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showCheckLogTest() throws Exception {
        // GIVEN
        given(diaryService.getList(anyLong())).willReturn(List.of(diary));
        given(diaryService.findHistory(anyLong()))
                .willReturn(List.of(DiaryFactory.diaryCheckLog(diary)));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/diary/checklist"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 복용 기록 체크를 등록할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void toggleCheckSuccessTest() throws Exception {
        // GIVEN
        Long diaryId = 1L;
        given(diaryService.toggleCheck(any(Member.class), anyLong(), any(LocalDate.class)))
                .willReturn(RsData.successOf(diary));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/diary/check/{diaryId}", diaryId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

}
