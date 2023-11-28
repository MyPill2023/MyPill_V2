package com.mypill.domain.survey.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.survey.properties.SurveyProperties;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyServiceTests extends IntegrationTest {

    @Autowired
    private SurveyService surveyService;
    @Autowired
    private SurveyProperties surveyProperties;
    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("설문 시작 선택 사항 유효성 검사 통과 성공")
    void validStartSurveySuccessTest() {
        // GIVEN
        int length = surveyProperties.getStartMaxLength() - surveyProperties.getStartMinLength();
        Long[] categoryItemIds = new Long[length];

        // WHEN
        RsData<String> rsData = surveyService.validStartSurvey(categoryItemIds);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
    }

    @Test
    @DisplayName("설문 시작 선택 사항 유효성 검사 통과 실패 - 최대값 이상 항목 선택")
    void validStartSurveyFailTest_TooManySelections() {
        // GIVEN
        int length = surveyProperties.getStartMaxLength() + 1;
        Long[] categoryItemIds = new Long[length];

        // WHEN
        RsData<String> rsData = surveyService.validStartSurvey(categoryItemIds);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("설문 시작 선택 사항 유효성 검사 통과 실패 - 최소값 이하 항목 선택")
    void validStartSurveyFailTest_TooFewSelections() {
        // GIVEN
        int length = Math.max(0, surveyProperties.getStartMinLength() - 1);
        Long[] categoryItemIds = new Long[length];

        // WHEN
        RsData<String> rsData = surveyService.validStartSurvey(categoryItemIds);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("설문 질문 선택 사항 유효성 검사 통과 성공")
    void validCompleteSurveySuccessTest() {
        // GIVEN
        int length = surveyProperties.getCompleteMaxLength() - surveyProperties.getCompleteMinLength();
        Long[] categoryItemIds = new Long[length];

        // WHEN
        RsData<String> rsData = surveyService.validCompleteSurvey(categoryItemIds);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
    }

    @Test
    @DisplayName("설문 질문 선택 사항 유효성 검사 통과 실패 - 최대값 이상 항목 선택")
    void validCompleteSurveyFailTest_TooManySelections() {
        // GIVEN
        int length = surveyProperties.getCompleteMaxLength() + 1;
        Long[] categoryItemIds = new Long[length];

        // WHEN
        RsData<String> rsData = surveyService.validCompleteSurvey(categoryItemIds);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("설문 질문 선택 사항 유효성 검사 통과 실패 - 최소값 이하 항목 선택")
    void validCompleteSurveyFailTest_TooFewSelections() {
        // GIVEN
        int length = Math.max(0, surveyProperties.getCompleteMinLength() - 1);
        Long[] categoryItemIds = new Long[length];

        // WHEN
        RsData<String> rsData = surveyService.validCompleteSurvey(categoryItemIds);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("선택 사항에서 영양제 정보 추출")
    void getAnswersSuccessTest() {
        // GIVEN
        Long[] questionIds = new Long[]{1L, 2L};

        // WHEN
        Map<String, List<Nutrient>> answers = surveyService.getAnswers(questionIds);

        // THEN
        assertThat(answers).isNotNull();
    }

    @Test
    @DisplayName("설문 결과 회원 정보에 저장")
    void extractedSuccessTest() {
        // GIVEN
        Member member = memberRepository.save(MemberFactory.member("testMember"));
        Map<String, List<Nutrient>> answers = new HashMap<>();

        // WHEN
        surveyService.extracted(answers, member);

        // THEN
        assertThat(member.getSurveyNutrients()).isNotNull();
    }


}
