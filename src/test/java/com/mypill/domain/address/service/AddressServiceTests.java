package com.mypill.domain.address.service;

import com.mypill.common.factory.AddressFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
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
    private MemberRepository memberRepository;
    private Member testBuyer1;
    private Member testBuyer2;

    @BeforeEach
    void beforeEachTest() {
        testBuyer1 = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
        testBuyer2 = memberRepository.save(MemberFactory.member("testBuyer2", Role.BUYER));
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void testCreateSuccess() {
        //WHEN
        RsData<Address> createRsData = addressService.create(AddressFactory.mockAddressRequest("testAddress"), testBuyer1);

        //THEN
        assertThat(createRsData.getResultCode()).isEqualTo("S-1");
        Address newAddress = createRsData.getData();
        assertThat(newAddress.getName()).isEqualTo("testAddress");
        assertTrue(newAddress.isDefault());
    }

    @Test
    @DisplayName("배송지 추가 실패 - 최대 등록 가능 개수 초과")
    void testCreateFail() {
        //GIVEN
        AddressRequest request = AddressFactory.mockAddressRequest("testAddress");
        for (int i = 0; i < AppConfig.getMaxAddressCount(); i++) {
            addressService.create(request, testBuyer1);
        }
        //WHEN
        RsData<Address> createRsData = addressService.create(request, testBuyer1);

        //THEN
        assertThat(createRsData.getResultCode()).isEqualTo("F-2");
        assertThat(addressService.findByMemberId(testBuyer1.getId())).hasSize(AppConfig.getMaxAddressCount());

    }

    @Test
    @DisplayName("배송지 가져오기 - 성공")
    void testGetSuccess() {
        //WHEN
        Address address = addressService.create(AddressFactory.mockAddressRequest("testAddress"), testBuyer1).getData();

        //THEN
        assertThat(addressService.get(testBuyer1, address.getId()).getData()).isEqualTo(address);
    }

    @Test
    @DisplayName("배송지 가져오기 실패 - 권한 없음")
    void testGetFail03() {
        //GIVEN
        Address address = addressService.create(AddressFactory.mockAddressRequest("testAddress"), testBuyer1).getData();

        //WHEN
        RsData<Address> getRsData = addressService.get(testBuyer2, address.getId());

        //THEN
        assertThat(getRsData.getResultCode()).isEqualTo("F-3");
        assertThat(getRsData.getData()).isNull();
    }

    @Test
    @DisplayName("배송지 수정 성공")
    void testUpdateSuccess() {
        //GIVEN
        Address address = addressService.create(AddressFactory.mockAddressRequest("testAddress"), testBuyer1).getData();

        //WHEN
        Address newAddress = addressService.update(testBuyer1, address.getId(), AddressFactory.mockAddressRequest("newAddress")).getData();

        //THEN
        assertThat(newAddress.getName()).isEqualTo("newAddress");
        assertTrue(newAddress.isDefault());
    }

    @Test
    @DisplayName("배송지 삭제 성공")
    void testDeleteSuccess() {
        //GIVEN
        Address address = addressService.create(AddressFactory.mockAddressRequest("testAddress"), testBuyer1).getData();

        //WHEN
        Address deletedAddress = addressService.softDelete(testBuyer1, address.getId()).getData();

        //THEN
        assertThat(deletedAddress.getDeleteDate()).isNotNull();
    }

}
