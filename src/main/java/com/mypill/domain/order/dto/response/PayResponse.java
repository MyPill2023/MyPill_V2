package com.mypill.domain.order.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PayResponse {
    private Order order;
    private String orderNumber;
    private String message;
    private String code;

    public static PayResponse of(Order order) {
        return PayResponse.builder()
                .order(order)
                .build();
    }

    public static PayResponse of(PayRequest payRequest, JsonNode failNode) {
        return PayResponse.builder()
                .orderNumber(payRequest.getOrderId())
                .message(failNode.get("message").asText())
                .code(failNode.get("code").asText())
                .build();
    }

    public static PayResponse of(PayRequest payRequest, String message, String code) {
        return PayResponse.builder()
                .orderNumber(payRequest.getOrderId())
                .message(message)
                .code(code)
                .build();
    }

    public static PayResponse of(JsonNode failNode) {
        return PayResponse.builder()
                .message(failNode.get("message").asText())
                .code(failNode.get("code").asText())
                .build();
    }
}