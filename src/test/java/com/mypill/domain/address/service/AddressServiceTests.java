package com.mypill.domain.address.service;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AddressServiceTests {

    @Autowired
    private AddressService addressService;
    @Autowired
    private MemberService memberService;
    private Member testUser1;
    private Member testUser2;

    @BeforeEach
    void beforeEachTest() {
        testUser1 = memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "구매자"), true).getData();
        testUser2 = memberService.join(new JoinRequest("testUser2", "김영희", "1234", "test2@test.com", "구매자"), true).getData();
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void testCreateSuccess() {
        //WHEN
        RsData<Address> createRsData = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1);

        //THEN
        assertThat(createRsData.getResultCode()).isEqualTo("S-1");
        Address newAddress = createRsData.getData();
        assertThat(newAddress.getName()).isEqualTo("김철수의 집");
        assertThat(newAddress.getAddress()).isEqualTo("서울시 강남구");
        assertThat(newAddress.getPhoneNumber()).isEqualTo("01012341234");
        assertTrue(newAddress.isDefault());

    }

    @Test
    @DisplayName("배송지 추가 실패 - 최대 등록 가능 개수 초과")
    void testCreateFail() {
        //GIVEN
        for (int i = 0; i < AppConfig.getMaxAddressCount(); i++) {
            addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1);
        }
        //WHEN
        RsData<Address> createRsData = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1);

        //THEN
        assertThat(createRsData.getResultCode()).isEqualTo("F-2");
        assertThat(addressService.findByMemberId(testUser1.getId())).hasSize(AppConfig.getMaxAddressCount());

    }

    @Test
    @DisplayName("배송지 가져오기 - 성공")
    void testGetSuccess() {
        //WHEN
        Address address = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();

        //THEN
        assertThat(addressService.get(testUser1, address.getId()).getData()).isEqualTo(address);
    }

    @Test
    @DisplayName("배송지 가져오기 실패 - 권한 없음")
    void testGetFail03() {
        //GIVEN
        Address address = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();

        //WHEN
        RsData<Address> getRsData = addressService.get(testUser2, address.getId());

        //THEN
        assertThat(getRsData.getResultCode()).isEqualTo("F-3");
        assertThat(getRsData.getData()).isNull();
    }

    @Test
    @DisplayName("배송지 수정 성공")
    void testUpdateSuccess() {
        //GIVEN
        Address address = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();

        //WHEN
        Address newAddress = addressService.update(testUser1, address.getId(), new AddressRequest("수정 주소 이름", "수정 이름", "수정 주소", "수정 상세 주소", "11111", "01056785678", true)).getData();

        //THEN
        assertThat(newAddress.getName()).isEqualTo("수정 주소 이름");
        assertThat(newAddress.getReceiverName()).isEqualTo("수정 이름");
        assertThat(newAddress.getAddress()).isEqualTo("수정 주소");
        assertThat(newAddress.getPhoneNumber()).isEqualTo("01056785678");
        assertTrue(newAddress.isDefault());
    }

    @Test
    @DisplayName("배송지 삭제 성공")
    void testDeleteSuccess() {
        //GIVEN
        Address address = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();

        //WHEN
        Address deletedAddress = addressService.softDelete(testUser1, address.getId()).getData();

        //THEN
        assertThat(deletedAddress.getDeleteDate()).isNotNull();
    }

}
