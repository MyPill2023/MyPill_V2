package com.mypill.domain.seller.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class SellerServiceTest extends IntegrationTest {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private MemberRepository memberRepository;
    private Member testMember;
    private final String brno = "BUSINESS_NUMBER";
    private final String nBrno = "NUTRIENT_BUSINESS_NUMBER";

    @BeforeEach
    void beforeEach() {
        testMember = memberRepository.save(MemberFactory.member("testMember", Role.WAITER));
    }

    @Test
    @DisplayName("통신판매업 번호 확인 - 인증 성공")
    void businessNumberCheckSuccessTest() {
        // WHEN
        sellerService.businessNumberCheck(brno, testMember);

        // THEN
        assertThat(testMember.getBusinessNumber()).isNull();
    }

    @Test
    @DisplayName("통신판매업 번호 확인 - 인증 실패 - 이미 등록된 번호")
    void businessNumberCheckFailTest_AlreadyRegisteredNumber() {
        // WHEN
        sellerService.businessNumberCheck(brno, testMember);

        // THEN
        assertThat(testMember.getBusinessNumber()).isNull();
        assertThat(testMember.getRole()).isEqualTo(Role.WAITER);
    }

    @Test
    @DisplayName("건강기능식품 판매업 번호 확인 - 인증 성공")
    void nutrientBusinessNumberCheckSuccessTest() {
        // WHEN
        sellerService.nutrientBusinessNumberCheck(nBrno, testMember);

        // THEN
        assertThat(testMember.getNutrientBusinessNumber()).isNull();
    }

    @Test
    @DisplayName("건강기능식품 판매업 번호 확인 - 인증 실패 - 이미 등록된 번호")
    void nutrientBusinessNumberCheckFailTest_AlreadyRegisteredNumber() {
        // WHEN
        sellerService.nutrientBusinessNumberCheck(nBrno, testMember);

        // THEN
        assertThat(testMember.getNutrientBusinessNumber()).isNull();
        assertThat(testMember.getRole()).isEqualTo(Role.WAITER);
    }
}