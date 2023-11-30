package com.mypill.domain.diary.service;


import com.mypill.common.factory.DiaryFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.diary.dto.request.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.repository.DiaryCheckLogRepository;
import com.mypill.domain.diary.repository.DiaryRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class DiaryServiceTests extends IntegrationTest {

    @Autowired
    private DiaryService diaryService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryCheckLogRepository diaryCheckLogRepository;

    private Member testBuyer;

    @BeforeEach
    void beforeEachTest() {
        testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
    }

    @Test
    @DisplayName("복용 영양제 등록 성공")
    void createSuccessTest() throws Exception {
        // GIVEN
        DiaryRequest diaryRequest = DiaryFactory.diaryRequest();

        // WHEN
        RsData<Diary> rsData = diaryService.create(diaryRequest, testBuyer);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getMember()).isEqualTo(testBuyer);
    }

    @Test
    @DisplayName("복용 스케줄 기록 체크 성공")
    void toggleCheckSuccessTest() throws Exception {
        // GIVEN
        Diary diary = diaryRepository.save(DiaryFactory.diary(testBuyer));

        // WHEN
        RsData<Diary> rsData = diaryService.toggleCheck(testBuyer, diary.getId(), LocalDate.now());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getTimeChecks()).hasSize(1);
    }

    @Test
    @DisplayName("복용 스케줄 기록 체크 실패 - 존재하지 않는 영양제")
    void toggleCheckFailTest_NonExist() throws Exception {
        // WHEN
        RsData<Diary> rsData = diaryService.toggleCheck(testBuyer, 1L, LocalDate.now());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("복용 스케줄 기록 체크 실패 - 다른 회원의 스케줄(권한 없음)")
    void toggleCheckFailTest_Unauthorized() throws Exception {
        // GIVEN
        Member testBuyer2 = memberRepository.save(MemberFactory.member("testBuyer2", Role.BUYER));
        Diary diary = diaryRepository.save(DiaryFactory.diary(testBuyer2));

        // WHEN
        RsData<Diary> rsData = diaryService.toggleCheck(testBuyer, diary.getId(), LocalDate.now());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("복용 영양제 삭제 성공")
    void deleteSuccessTest() throws Exception {
        // GIVEN
        Diary diary = diaryRepository.save(DiaryFactory.diary(testBuyer));

        // WHEN
        RsData<Diary> rsData = diaryService.delete(diary.getId(), testBuyer);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("복용 영양제 삭제 실패 - 존재하지 않는 영양제")
    void deleteFailTest_NonExist() throws Exception {
        // WHEN
        RsData<Diary> rsData = diaryService.delete(1L, testBuyer);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("복용 영양제 삭제 실패 - 다른 회원의 영양제(권한 없음)")
    void beforeUpdateFailTest_Unauthorized() throws Exception {
        // GIVEN
        Member testBuyer2 = memberRepository.save(MemberFactory.member("testBuyer2", Role.BUYER));
        Diary diary = diaryRepository.save(DiaryFactory.diary(testBuyer2));

        // WHEN
        RsData<Diary> rsData = diaryService.delete(diary.getId(), testBuyer);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("복용 체크 기록 조회")
    void findHistorySuccessTest() throws Exception {
        // GIVEN
        Diary diary = diaryRepository.save(DiaryFactory.diary(testBuyer));
        DiaryCheckLog diaryCheckLog = diaryCheckLogRepository.save(DiaryFactory.diaryCheckLog(diary));

        // WHEN
        List<DiaryCheckLog> history = diaryService.findHistory(testBuyer.getId());

        // THEN
        assertThat(history).hasSize(1);
    }

    @Test
    @DisplayName("복용 등록한 영양제 목록 조회")
    void getListSuccessTest() throws Exception {
        // GIVEN
        diaryRepository.save(DiaryFactory.diary(testBuyer));
        diaryRepository.save(DiaryFactory.diary(testBuyer));

        // WHEN
        List<Diary> list = diaryService.getList(testBuyer.getId());

        // THEN
        assertThat(list).hasSize(2);
    }
}
