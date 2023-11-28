package com.mypill.domain.seller.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.OrderFactory;
import com.mypill.common.factory.ProductFactory;
import com.mypill.common.fixture.TProduct;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SellerControllerTests extends ControllerTest {

    @Test
    @DisplayName("판매자 회원은 주문 관리 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void showMyOrderSuccessTest() throws Exception {
        // GIVEN
        Member testBuyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        Order order = OrderFactory.order(testBuyer, OrderFactory.paymentDone());
        OrderItem orderItem = OrderFactory.orderItem(order, TProduct.PRODUCT_1.getProduct());
        Map<OrderStatus, Long> orderStatusCount = new HashMap<>();

        given(rq.getMember()).willReturn(testBuyer);
        given(orderService.findBySellerId(anyLong())).willReturn(List.of(order));
        given(orderService.findOrderItemBySellerId(anyLong())).willReturn(List.of(orderItem));
        given(orderService.getOrderStatusCount(any())).willReturn(orderStatusCount);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/seller/order"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("대기자는 판매자 인증 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "WAITER")
    void showCertificateSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/seller/certificate"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("대기자는 통신판매업 인증을 할 수 있다")
    @WithMockUser(authorities = "WAITER")
    void brnoCertificateSuccessTest() throws Exception {
        // GIVEN
        Member testWaiter = MemberFactory.member(1L, "testBuyer", Role.WAITER);
        String businessNumber = "12345";

        given(rq.getMember()).willReturn(testWaiter);
        given(sellerService.businessNumberCheck(anyString(), any(Member.class)))
                .willReturn(RsData.successOf(testWaiter));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/seller/brnoCertificate")
                        .with(csrf())
                        .param("businessNumber", businessNumber)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("대기자는 건강기능식품 판매업 인증을 할 수 있다")
    @WithMockUser(authorities = "WAITER")
    void nBrnoCertificateSuccessTest() throws Exception {
        // GIVEN
        Member testWaiter = MemberFactory.member(1L, "testBuyer", Role.WAITER);
        String nutrientBusinessNumber = "12345";

        given(rq.getMember()).willReturn(testWaiter);
        given(sellerService.nutrientBusinessNumberCheck(anyString(), any(Member.class)))
                .willReturn(RsData.successOf(testWaiter));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/seller/nBrnoCertificate")
                        .with(csrf())
                        .param("nutrientBusinessNumber", nutrientBusinessNumber)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자 회원은 상품 관리 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void showMyProductSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member(1L, "testSeller", Role.SELLER);
        Product product = ProductFactory.product("product", testSeller);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);

        given(rq.getMember()).willReturn(testSeller);
        given(productService.getAllProductBySellerId(anyLong(), any(PageRequest.class)))
                .willReturn(page);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/seller/myProduct")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("판매자 회원은 통계 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "SELLER")
    void showChartSuccessTest() throws Exception {
        // GIVEN
        Member testSeller = MemberFactory.member(1L, "testSeller", Role.SELLER);
        Map<YearMonth, Long> yearMonthLongMap = new HashMap<>();

        given(rq.getMember()).willReturn(testSeller);
        given(orderService.countOrderPrice(anyLong())).willReturn(yearMonthLongMap);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/seller/chart"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

}
