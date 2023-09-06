package com.mypill.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationTypeCode {
    OrderPayment("OrderPayment", "결제 후 주문 생성"),
    OrderStatus("OrderStatus", "주문 상태 변경"),
    OrderCanceled("OrderCanceled", "주문 취소"),
    Record("Record", "복약 기록 알림");

    private final String value;
    private final String description;

    NotificationTypeCode(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
