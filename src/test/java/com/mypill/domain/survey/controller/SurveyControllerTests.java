package com.mypill.domain.survey.controller;

import com.mypill.domain.ControllerTest;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.question.entity.Question;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SurveyControllerTests extends ControllerTest {

    @Test
    @DisplayName("구매자는 설문 가이드 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showGuideSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/survey/guide"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 설문 시작 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showStartSuccessTest() throws Exception {
        // GIVEN
        given(categoryService.findAll()).willReturn(List.of(new Category()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/survey/start"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 설문을 진행할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showStepSuccessTest() throws Exception {
        // GIVEN
        Map<String, String> param = new HashMap<>();

        given(surveyService.validStartSurvey(any(Long[].class))).willReturn(RsData.successOf(""));
        given(questionService.findByCategoryId(anyLong())).willReturn(List.of(new Question()));
        given(categoryService.getCategory(anyLong())).willReturn(new Category());

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/survey/step")
                        .param("category_1", objectMapper.writeValueAsString(param))
                        .param("question_1", objectMapper.writeValueAsString(param))
                        .param("stepNo", "1")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자는 설문 결과를 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showCompleteSuccessTest() throws Exception {
        // GIVEN
        Map<String, String> param = new HashMap<>();
        Map<String, List<Nutrient>> answers = new HashMap<>();

        given(rq.isLogin()).willReturn(true);
        given(rq.getMember()).willReturn(new Member());
        given(surveyService.validCompleteSurvey(any(Long[].class))).willReturn(RsData.successOf(""));
        given(surveyService.getAnswers(any(Long[].class))).willReturn(answers);
        doNothing().when(surveyService).extracted(any(Map.class), any(Member.class));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/survey/complete")
                        .param("category_1", objectMapper.writeValueAsString(param))
                        .param("question_1", objectMapper.writeValueAsString(param))
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }




}
