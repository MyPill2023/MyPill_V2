package com.mypill.domain.buyer.controller;

import com.mypill.common.factory.AddressFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.common.fixture.TOrder;
import com.mypill.common.fixture.TProduct;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BuyerControllerTest extends ControllerTest {

    @Test
    @DisplayName("구매자는 좋아요 표시한 상품의 목록을 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showMyLikesSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        List<Long> productIds = Arrays.asList(1L, 2L);
        List<Product> products = Arrays.asList(TProduct.PRODUCT_1.getProduct(), TProduct.PRODUCT_2.getProduct());

        given(rq.getMember()).willReturn(testBuyer);
        given(productLikeService.findProductIdsByMemberId(anyLong())).willReturn(productIds);
        given(productService.findByIdIn(anyList())).willReturn(products);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myLikes"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("구매자는 자신의 복약관리 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showMyScheduleSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/mySchedule"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
        ;
    }


    @Test
    @DisplayName("구매자는 자신의 주문 목록을 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showMyOrderSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        List<Order> orders = List.of(TOrder.ORDER_1.getOrder());
        List<OrderItem> orderItems = TOrder.ORDER_1.getOrder().getOrderItems();

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.findByBuyerId(anyLong())).willReturn(orders);
        given(orderService.findOrderItemByBuyerId(anyLong())).willReturn(orderItems);
        given(orderService.getOrderStatusCount(anyList())).willReturn(new HashMap<>());


        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myOrder"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("구매자는 자신의 배송지 목록을 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showMyAddressSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        List<Address> addresses = List.of(AddressFactory.address(testBuyer));

        given(rq.getMember()).willReturn(testBuyer);
        given(addressService.findByMemberId(anyLong())).willReturn(addresses);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myAddress"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("구매자는 자신의 설문 결과를 조회할 수 있다")
    @WithMockUser(authorities = "BUYER")
    void showMySurveySuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        given(rq.getMember()).willReturn(testBuyer);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/mySurvey"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
        ;
    }
}