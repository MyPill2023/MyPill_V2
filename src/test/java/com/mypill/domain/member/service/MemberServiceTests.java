package com.mypill.domain.member.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.attr.service.AttrService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class MemberServiceTests {
    @Autowired
    private MemberService memberService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("자체회원가입 테스트")
    void joinTest() {
        // GIVEN
        JoinRequest joinRequest = MemberFactory.joinRequest("testMember");

        // WHEN
        Member testMember = memberService.join(joinRequest).getData();

        // THEN
        assertThat(testMember).isNotNull();
        assertThat(testMember.getUsername()).isEqualTo(joinRequest.getUsername());
        assertThat(testMember.getName()).isEqualTo(joinRequest.getName());
        assertThat(testMember.getEmail()).isEqualTo(joinRequest.getEmail());
    }

    @Test
    @DisplayName("소셜로그인 테스트")
    void whenSocialLoginTest() {
        // GIVEN
        String providerCode = "K";
        String username = "testMember";
        String name = "name";
        String email = "testMember@test.com";

        // WHEN
        Member testMember = memberService.whenSocialLogin(providerCode, username, name, email).getData();

        // THEN
        assertThat(testMember).isNotNull();
        assertThat(testMember.getUsername()).isEqualTo(username);
        assertThat(testMember.getName()).isEqualTo(name);
        assertThat(testMember.getRole()).isEqualTo(Role.BUYER);
        assertThat(testMember.getEmail()).isEqualTo(email);
        assertThat(testMember.getProviderTypeCode()).isEqualTo(providerCode);
    }

    @Test
    @DisplayName("ID로 회원검색 테스트")
    void findByIdTest() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));

        // WHEN
        Member member = memberService.findById(testMember.getId()).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(testMember.getId()).isEqualTo(member.getId());
        assertThat(testMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    @DisplayName("회원 아이디로 회원검색 테스트")
    void findByUsernameTest() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));

        // WHEN
        Member member = memberService.findByUsername(testMember.getUsername()).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(testMember.getId()).isEqualTo(member.getId());
        assertThat(testMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    @DisplayName("회원정보 - 이름 변경")
    void updateNameTest() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));
        String newName = "newName";

        // WHEN
        Member member = memberService.updateName(testMember, newName).getData();

        // THEN
        assertThat(member).isNotNull();
        assertThat(member.getUsername()).isEqualTo(testMember.getUsername());
        assertThat(member.getName()).isEqualTo(newName);
        assertThat(member.getRole()).isEqualTo(testMember.getRole());
        assertThat(member.getEmail()).isEqualTo(testMember.getEmail());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteAccountTest() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));

        // WHEN
        Member deletedMember = memberService.softDelete(testMember).getData();

        // THEN
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getUsername()).isEqualTo(testMember.getUsername());
        assertThat(deletedMember.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트1 : 입력값 없음")
    void usernameValidationTest1() {
        // GIVEN
        String username = "";

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.USERNAME_EMPTY);
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트2 : 중복")
    void usernameValidationTest2() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(testMember.getUsername());

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.USERNAME_DUPLICATE);
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트3 : 통과")
    void usernameValidationTest3() {
        // GIVEN
        String username = "testUser2";

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.VALIDATION_OK);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트1  : 입력값 없음")
    void emailValidationTest1() {
        // GIVEN
        String email = "";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_EMPTY);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트2 : 잘못된 형식")
    void emailValidationTest2() {
        // GIVEN
        String email = "testEmail@test,com";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_FORMAT_ERROR);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트3 : 중복")
    void emailValidationTest3() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));

        // WHEN
        EmailValidationResult result = memberService.emailValidation(testMember.getEmail());

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_DUPLICATE);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트4 : 통과")
    void emailValidationTest4() {
        // GIVEN
        String email = "testEmail2@test.com";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.VALIDATION_OK);
    }

    @Test
    @DisplayName("이메일 인증")
    void verifyEmailTest() {
        // GIVEN
        Member testMember = memberRepository.save(MemberFactory.member("testMember"));
        String emailVerificationCode = "member__%d__extra__emailVerificationCode".formatted(testMember.getId());
        String attr = attrService.get(emailVerificationCode, "");

        // WHEN
        String resultCode = memberService.verifyEmail(testMember.getId(), attr).getResultCode();

        // THEN
        assertThat(resultCode).startsWith("S-");
        assertTrue(memberRepository.findById(testMember.getId()).isPresent());
        assertTrue(memberRepository.findById(testMember.getId()).get().isEmailVerified());
    }
}