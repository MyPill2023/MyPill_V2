package com.mypill.domain.member.service;

import com.mypill.domain.email.service.EmailVerificationService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import com.mypill.global.event.EventAfterDeleteMember;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class MemberServiceUnitTests {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Spy
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private ApplicationEventPublisher publisher;
    private Member testUser;

    @BeforeEach
    void beforeEach() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        JoinRequest request = joinRequest("testUser", "test1@test.com");
        String encryptedPw = encoder.encode(request.getPassword());
        testUser = Member.of(request, encryptedPw);
    }

    private JoinRequest joinRequest(String username, String email){
        return JoinRequest.builder()
                .username(username)
                .name("김철수")
                .password("1234")
                .email(email)
                .userType("구매자")
                .build();
    }

    @Test
    @DisplayName("회원가입")
    void joinSuccessUnitTest() {
        // GIVEN
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        JoinRequest request = joinRequest("newTestUser", "newTest@test.com");
        String encryptedPw = encoder.encode(request.getPassword());
        doReturn(Member.of(request, encryptedPw)).when(memberRepository).save(any(Member.class));

        // WHEN
        Member member = memberService.join(request, false).getData();

        // THEN
        assertThat(member.getEmail()).isEqualTo(request.getEmail());
        assertThat(member.getRole()).isEqualTo(Role.BUYER);
        assertThat(encoder.matches(request.getPassword(), member.getPassword())).isTrue();

        // verify
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
    }

    @Test
    @DisplayName("whenSocialLogin")
    void whenSocialLoginSuccessUnitTest() {
        // GIVEN
        String providerCode = "K";

        // WHEN
        Member savedMember = memberService.whenSocialLogin(providerCode, "testUser3", "김짱구", "test9@test.com").getData();

        // THEN
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo("testUser3");
        assertThat(savedMember.getName()).isEqualTo("김짱구");
        assertThat(savedMember.getRole()).isEqualTo(Role.BUYER);
        assertThat(savedMember.getEmail()).isEqualTo("test9@test.com");
        assertThat(savedMember.getProviderTypeCode()).isEqualTo(providerCode);
    }

    @Test
    @DisplayName("updateName")
    void updateNameSuccessUnitTest() {
        // GIVEN
        String newName = "손흥민";

        // WHEN
        Member member = memberService.updateName(testUser, newName).getData();

        // THEN
        assertThat(member).isNotNull();
        assertThat(member.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(member.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("softDelete")
    void softDeleteSuccessUnitTest() {
        // GIVEN
        doNothing().when(publisher).publishEvent(any(EventAfterDeleteMember.class));

        // WHEN
        Member deletedMember = memberService.softDelete(testUser).getData();

        // THEN
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(deletedMember.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("findById")
    void findByIdSuccessUnitTest() {
        // given
        doReturn(Optional.of(testUser)).when(memberRepository).findById(1L);

        // WHEN
        Member member = memberService.findById(1L).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(testUser.getUsername()).isEqualTo(member.getUsername());
        assertThat(testUser.getName()).isEqualTo(member.getName());

        // verify
        verify(memberRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("findByUsername")
    void findByUsernameSuccessUnitTest() {
        // GIVEN
        doReturn(Optional.of(testUser)).when(memberRepository).findByUsername(testUser.getUsername());

        // WHEN
        Member member = memberService.findByUsername(testUser.getUsername()).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(testUser.getId()).isEqualTo(member.getId());
        assertThat(testUser.getUsername()).isEqualTo(member.getUsername());

        // verify
        verify(memberRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    @DisplayName("usernameValidation - USERNAME_EMPTY")
    void usernameValidationUnitTestForEmptyUsername() {
        // GIVEN
        String username = "";

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.USERNAME_EMPTY);
    }

    @Test
    @DisplayName("usernameValidation - USERNAME_DUPLICATE")
    void usernameValidationUnitTestForDuplicatedUsername() {
        // GIVEN
        String username = testUser.getUsername();
        doReturn(Optional.of(testUser)).when(memberRepository).findByUsername(testUser.getUsername());

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.USERNAME_DUPLICATE);
    }

    @Test
    @DisplayName("usernameValidation - VALIDATION_OK")
    void usernameValidationUnitTestForValidUsername() {
        // GIVEN
        String username = "testUser2";

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.VALIDATION_OK);
    }

    @Test
    @DisplayName("EmailValidationResult - EMAIL_EMPTY")
    void emailValidationUnitTestForEmptyEmail() {
        // GIVEN
        String email = "";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_EMPTY);
    }

    @Test
    @DisplayName("EmailValidationResult - EMAIL_FORMAT_ERROR")
    void emailValidationTestForEmailFormatError() {
        // GIVEN
        String email = "testEmail@test,com";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_FORMAT_ERROR);
    }

    @Test
    @DisplayName("EmailValidationResult - EMAIL_DUPLICATE")
    void emailValidationUnitTestForDuplicatedEmail() {
        // GIVEN
        String email = testUser.getEmail();
        doReturn(Optional.of(testUser)).when(memberRepository).findByEmail(testUser.getEmail());

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_DUPLICATE);
    }

    @Test
    @DisplayName("EmailValidationResult - VALIDATION_OK")
    void emailValidationUnitTestForValidEmail() {
        // GIVEN
        String email = "testEmail2@test.com";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.VALIDATION_OK);
    }

    @Test
    @DisplayName("verifyEmail")
    void verifyEmailSuccessUnitTest() {
        // GIVEN
        doReturn(RsData.of("S-1", "")).when(emailVerificationService).verifyVerificationCode(anyLong(), anyString());
        doReturn(Optional.of(testUser)).when(memberRepository).findById(1L);

        // WHEN
        RsData result = memberService.verifyEmail(1L, "verificationCode");

        // THEN
        assertThat(result.isSuccess()).isTrue();
        assertTrue(memberRepository.findById(1L).isPresent());
        assertTrue(memberRepository.findById(1L).get().isEmailVerified());
    }

}
