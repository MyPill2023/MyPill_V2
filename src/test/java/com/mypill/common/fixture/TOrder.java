package com.mypill.common.fixture;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.OrderFactory;
import com.mypill.common.mock.MockOrder;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.Payment;

import java.util.List;

public enum TOrder {

    ORDER_1(1L),
    ORDER_2(2L);

    private Long id;

    TOrder(Long id){
        this.id = id;
    }

    public Order getOrder(){

        Member buyer = MemberFactory.member(1L, "testBuyer", Role.BUYER);
        List<OrderItem> orderItem = List.of(
                OrderFactory.orderItem(TProduct.PRODUCT_1.getProduct()),
                OrderFactory.orderItem(TProduct.PRODUCT_2.getProduct())
        );
        Payment payment = OrderFactory.paymentDone();

        return MockOrder.builder()
                .id(id)
                .buyer(buyer)
                .orderItems(orderItem)
                .totalPrice(12000L)
                .payment(payment)
                .build();
    }

}
