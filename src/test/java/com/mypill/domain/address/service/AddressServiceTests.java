package com.mypill.domain.address.service;

import com.mypill.common.factory.AddressFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.repository.AddressRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


class AddressServiceTests extends IntegrationTest {

    @Autowired
    private AddressService addressService;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;
    private Member testBuyer;
    private Member testBuyer2;

    @BeforeEach
    void beforeEachTest() {
        testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void createSuccessTest() {
        //WHEN
        RsData<Address> rsData = addressService.create(AddressFactory.addressRequest("testAddress"), testBuyer);

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getName()).isEqualTo("testAddress");
    }

    @Test
    @DisplayName("배송지 추가 실패 - 최대 등록 가능 개수 초과")
    void createFailTest_exceedingMaxAllowableCount() {
        //GIVEN
        AddressRequest request = AddressFactory.addressRequest("testAddress");
        for (int i = 0; i < AppConfig.getMaxAddressCount(); i++) {
            addressRepository.save(AddressFactory.address(testBuyer, String.valueOf(i)));
        }
        //WHEN
        RsData<Address> rsData = addressService.create(request, testBuyer);

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("배송지 가져오기 - 성공")
    void getSuccessTest() {
        // GIVEN
        Address address =  addressRepository.save(AddressFactory.address(testBuyer));

        //WHEN
        RsData<Address> rsData = addressService.get(testBuyer, address.getId());

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData()).isEqualTo(address);
    }

    @Test
    @DisplayName("배송지 가져오기 실패 - 존재하지 않는 배송지")
    void getFailTest_NonExistentAddress() {
        //WHEN
        RsData<Address> rsData = addressService.get(testBuyer2, 1L);

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("배송지 가져오기 실패 - 삭제된 배송지")
    void getFailTest_deletedAddress() {
        //GIVEN
        Address address =  addressRepository.save(AddressFactory.deletedAddress(testBuyer));

        //WHEN
        RsData<Address> rsData = addressService.get(testBuyer2, address.getId());

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("배송지 가져오기 실패 - 권한 없음")
    void getFailTest_Unauthorized() {
        //GIVEN
        Address address =  addressRepository.save(AddressFactory.address(testBuyer));
        testBuyer2 = memberRepository.save(MemberFactory.member("testBuyer2", Role.BUYER));

        //WHEN
        RsData<Address> rsData = addressService.get(testBuyer2, address.getId());

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-3");
    }

    @Test
    @DisplayName("배송지 수정 성공")
    void updateSuccessTest() {
        //GIVEN
        Address address =  addressRepository.save(AddressFactory.address(testBuyer, "testAddress"));

        //WHEN
        RsData<Address> rsData = addressService.update(testBuyer, address.getId(), AddressFactory.addressRequest("newAddress", false));

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getName()).isEqualTo("newAddress");
        assertThat(rsData.getData().isDefault()).isFalse();
    }

    @Test
    @DisplayName("배송지 삭제 성공")
    void softDeleteSuccessTest() {
        //GIVEN
        Address address =  addressRepository.save(AddressFactory.address(testBuyer));

        //WHEN
        RsData<Address> rsData= addressService.softDelete(testBuyer, address.getId());

        //THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getDeleteDate()).isNotNull();
    }

}
