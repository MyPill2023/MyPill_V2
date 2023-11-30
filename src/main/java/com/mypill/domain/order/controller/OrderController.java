package com.mypill.domain.order.controller;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.dto.response.OrderManagementResponse;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.dto.response.PayResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.order.service.TossPaymentService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
@Tag(name = "OrderController", description = "주문")
public class OrderController {

    private final OrderService orderService;
    private final TossPaymentService tossPaymentService;
    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/form/{orderId}")
    @Operation(summary = "주문하기 페이지")
    public String showOrderForm(@PathVariable Long orderId, Model model) {
        RsData<Order> orderRsData = orderService.getOrderForm(rq.getMember(), orderId);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        List<AddressResponse> addresses = addressService.findByMemberId(rq.getMember().getId()).stream()
                .map(AddressResponse::of)
                .toList();
        AddressResponse defaultAddress = addresses.stream()
                .filter(AddressResponse::isDefault)
                .findFirst().orElse(null);
        model.addAttribute("order", OrderResponse.of(orderRsData.getData()));
        model.addAttribute("addresses", addresses);
        model.addAttribute("defaultAddress", defaultAddress);
        return "usr/order/form";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/all")
    @Operation(summary = "장바구니의 전체 상품 주문")
    public String createFromCart() {
        RsData<Order> orderRsData = orderService.createFromCart(rq.getMember());
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/selected")
    @Operation(summary = "장바구니에서 선택한 상품만 주문")
    public String createFromSelected(@RequestParam String[] selectedCartProductIds) {
        if (selectedCartProductIds.length == 0) {
            return rq.historyBack("선택된 상품이 없습니다.");
        }
        RsData<Order> orderRsData = orderService.createFromSelectedCartProduct(rq.getMember(), selectedCartProductIds);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/single")
    @Operation(summary = "개별 상품 바로 주문")
    public String createFromSingleProduct(@RequestParam Long productId, @RequestParam Long quantity) {
        RsData<Order> orderRsData = orderService.createSingleProduct(rq.getMember(), productId, quantity);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/detail/{orderId}")
    @Operation(summary = "구매자의 주문 내역 조회 페이지")
    public String showOrderDetail(@PathVariable Long orderId, Model model) {
        RsData<Order> orderDetailRsData = orderService.getOrderDetails(rq.getMember(), orderId);
        if (orderDetailRsData.isFail()) {
            return rq.historyBack(orderDetailRsData);
        }
        model.addAttribute("order", OrderResponse.of(orderDetailRsData.getData()));
        return "usr/order/detail";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/success")
    @Operation(summary = "결제 요청 성공")
    public String confirmPayment(PayRequest payRequest, Model model) {
        RsData<Order> validateRsData = orderService.checkIfOrderCanBePaid(payRequest);
        if (validateRsData.isFail()) {
            return rq.historyBack(validateRsData);
        }
        RsData<?> payRsData = tossPaymentService.pay(validateRsData.getData(), payRequest);
        if (payRsData.isFail()) {
            model.addAttribute("payResponse", payRsData.getData());
            return rq.historyBack(payRsData);
        }
        return rq.redirectWithMsg("/order/detail/%s".formatted(((Order) payRsData.getData()).getId()), payRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/fail")
    @Operation(summary = "결제 실패")
    public String failPayment(PayResponse payResponse, Model model) {
        model.addAttribute("payResponse", payResponse);
        return "usr/order/fail";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "주문 취소")
    public String cancel(@PathVariable Long orderId) {
        RsData<Order> checkRsData = orderService.checkIfOrderCanBeCanceled(rq.getMember(), orderId);
        if (checkRsData.isFail()) {
            return rq.historyBack(checkRsData);
        }
        RsData<?> cancelRsData = tossPaymentService.cancel(checkRsData.getData());
        if (cancelRsData.isFail()) {
            return rq.historyBack(cancelRsData);
        }
        return rq.redirectWithMsg("/order/detail/%s".formatted(orderId), cancelRsData);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/management/{orderId}")
    @Operation(summary = "판매자의 주문 관리 페이지")
    public String management(@PathVariable Long orderId, Model model) {
        RsData<Order> orderDetailRsData = orderService.getOrderDetails(rq.getMember(), orderId);
        if (orderDetailRsData.isFail()) {
            return rq.historyBack(orderDetailRsData);
        }
        Order order = orderDetailRsData.getData();
        List<OrderItem> orderItems = orderService.findByProductSellerIdAndOrderId(rq.getMember().getId(), orderId);
        model.addAttribute("response", OrderManagementResponse.of(order, orderItems));
        return "usr/order/management";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/update/status/{orderItemId}")
    @Operation(summary = "판매자의 주문 상태 업데이트")
    public String updateOrderStatus(@PathVariable Long orderItemId, @RequestParam String newStatus) {
        RsData<OrderItem> updateRsData = orderService.updateOrderItemStatus(orderItemId, newStatus);
        if (updateRsData.isFail()) {
            rq.historyBack(updateRsData);
        }
        return rq.redirectWithMsg("/order/management/%s".formatted(updateRsData.getData().getOrder().getId()), updateRsData);
    }
}
