package com.mypill.domain.address.controller;

import com.mypill.common.factory.AddressFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddressControllerTests extends ControllerTest {

    @Test
    @DisplayName("구매자 회원은 배송지를 등록 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showCreateFormTestSuccess() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.isFull(any(Long.class))).willReturn(false);

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myAddress/create"))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 배송지를 추가할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void createSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        AddressRequest request = AddressFactory.addressRequest("address");

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.create(any(AddressRequest.class), any(Member.class)))
                .willReturn(RsData.successOf(AddressFactory.address(testBuyer)));

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/buyer/myAddress/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("memberId", String.valueOf(testBuyer.getId()))
                        .param("name", request.getAddress())
                        .param("receiverName", request.getReceiverName())
                        .param("address", request.getAddress())
                        .param("detailAddress", request.getDetailAddress())
                        .param("postCode", request.getPostCode())
                        .param("phoneNumber", request.getPhoneNumber())
                        .param("isDefault", String.valueOf(request.isDefault()))
                )
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 배송지 수정 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void updateFormTestSuccess() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        Address address = AddressFactory.address(testBuyer);
        Long addressId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.get(any(Member.class), any(Long.class))).willReturn(RsData.successOf(address));

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myAddress/update/{addressId}", addressId))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 배송지를 수정할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void updateSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        AddressRequest request = AddressFactory.addressRequest("address");
        Long addressId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.update(any(Member.class), any(Long.class), any(AddressRequest.class)))
                .willReturn(RsData.successOf(AddressFactory.address(testBuyer)));

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/buyer/myAddress/update/{addressId}", addressId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("memberId", String.valueOf(testBuyer.getId()))
                        .param("name", request.getAddress())
                        .param("receiverName", request.getReceiverName())
                        .param("address", request.getAddress())
                        .param("detailAddress", request.getDetailAddress())
                        .param("postCode", request.getPostCode())
                        .param("phoneNumber", request.getPhoneNumber())
                        .param("isDefault", String.valueOf(request.isDefault()))
                )
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 배송지를 삭제할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void softDeleteSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        Long addressId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.softDelete(any(Member.class), any(Long.class)))
                .willReturn(RsData.successOf(AddressFactory.address(testBuyer)));

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/buyer/myAddress/delete/{addressId}", addressId)
                        .with(csrf())
                )
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 배송지 상세정보를 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void getAddressDetailsSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        Address address = AddressFactory.address(testBuyer);
        Long addressId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.findById(any(Long.class))).willReturn(Optional.ofNullable(address));

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myAddress/getAddressDetails")
                        .param("addressId", String.valueOf(addressId)))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 존재하지 않는 배송지의 상세정보를 조회할 수 없다")
    @WithMockUser(authorities = "BUYER")
    void getAddressDetailsFailTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer");
        Long addressId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.findById(any(Long.class))).willReturn(Optional.empty());

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myAddress/getAddressDetails")
                        .param("addressId", String.valueOf(addressId)))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(status().is4xxClientError());
    }

}
