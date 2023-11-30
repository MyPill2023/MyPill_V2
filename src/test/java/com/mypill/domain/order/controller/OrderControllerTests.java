package com.mypill.domain.order.controller;

import com.mypill.common.factory.AddressFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.OrderFactory;
import com.mypill.common.fixture.TProduct;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.dto.response.PayResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTests extends ControllerTest {

    @Test
    @DisplayName("구매자 회원은 주문 폼에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showOrderFormSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);
        Address address = AddressFactory.address(testBuyer);

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.getOrderForm(any(Member.class), anyLong())).willReturn(RsData.successOf(order));
        given(addressService.findByMemberId(anyLong())).willReturn(List.of(address));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/form/{orderId}", order.getId()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 장바구니에서 전체 상품을 주문할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void createFromCartSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.createFromCart(any(Member.class))).willReturn(RsData.successOf(order));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/create/all")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 장바구니에서 선택한 상품을 주문할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void createFromSelectedSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);
        String[] selectedCartProductIds = new String[]{"1", "2"};

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.createFromSelectedCartProduct(any(Member.class), any(String[].class)))
                .willReturn(RsData.successOf(order));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/create/selected")
                        .with(csrf())
                        .param("selectedCartProductIds", selectedCartProductIds)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 개별 상품을 바로 주문할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void createFromSingleProductSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.createSingleProduct(any(Member.class), anyLong(), anyLong()))
                .willReturn(RsData.successOf(order));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/create/single")
                        .with(csrf())
                        .param("productId", "1")
                        .param("quantity", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 주문 내역을 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showOrderDetailSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.getOrderDetails(any(Member.class), anyLong()))
                .willReturn(RsData.successOf(order));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/detail/{orderId}", order.getId()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("결제 요청 성공 경로")
    @WithMockUser(authorities = "BUYER")
    void confirmPaymentTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);
        PayRequest payRequest = OrderFactory.payRequest(order.getId(), 10L);

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.checkIfOrderCanBePaid(any(PayRequest.class)))
                .willReturn(RsData.successOf(order));
        given(tossPaymentService.pay(any(Order.class), any(PayRequest.class)))
                .willReturn(RsData.successOf(PayResponse.of(order)));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/success"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    @DisplayName("결제 요청 실패 경로")
    @WithMockUser(authorities = "BUYER")
    void failPaymentTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/fail"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("구매자 회원은 주문을 취소할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void cancelSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);

        RsData<?> rsData = new RsData<>();

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.checkIfOrderCanBeCanceled(any(Member.class), anyLong()))
                .willReturn(RsData.successOf(order));
        given(tossPaymentService.cancel(any(Order.class)))
                .willReturn(RsData.successOf(PayResponse.of(order)));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/cancel/{orderId}", order.getId())
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자 회원은 주문 관리 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void showManagementSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);
        OrderItem orderItem = OrderFactory.orderItem(order, TProduct.PRODUCT_1.getProduct());

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.getOrderDetails(any(Member.class), anyLong()))
                .willReturn(RsData.successOf(order));
        given(orderService.findByProductSellerIdAndOrderId(anyLong(), anyLong()))
                .willReturn(List.of(orderItem));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/management/{orderId}", order.getId()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자 회원은 주문 상태를 업데이트 할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void updateOrderStatusSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(1L, testBuyer);
        OrderItem orderItem = OrderFactory.orderItem(order, TProduct.PRODUCT_1.getProduct());
        Long orderItemId = 1L;

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.updateOrderItemStatus(anyLong(), anyString()))
                .willReturn(RsData.successOf(orderItem));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/update/status/{orderItemId}", orderItemId)
                        .with(csrf())
                        .param("newStatus", "newStatus")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

}
