package com.mypill.common.factory;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.order.entity.Order;

public class NotificationFactory {

    private NotificationFactory(){}

    public static Notification orderPayment(Member member){
        Order order = OrderFactory.order(1L, member);
        return createOrderPayment(member, order);
    }

    public static Notification orderPayment(Member member, Order order){
        return createOrderPayment(member, order);
    }

    private static Notification createOrderPayment(Member member, Order order){
        return Notification.builder()
                .typeCode(NotificationTypeCode.OrderPayment)
                .member(member)
                .order(order)
                .build();
    }

}
