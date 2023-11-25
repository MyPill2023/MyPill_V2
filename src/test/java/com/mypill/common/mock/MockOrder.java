package com.mypill.common.mock;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;

import java.util.List;

public class MockOrder {
    public MockOrder(){}

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private Long id;
        private String orderNumber;
        private String name;
        private Member buyer;
        private List<OrderItem> orderItems;
        private Long totalPrice;
        private Payment payment;
        private Address deliveryAddress;
        private OrderStatus primaryOrderStatus;

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder buyer(Member buyer){
            this.buyer = buyer;
            return this;
        }

        public Builder orderItems(List<OrderItem> orderItems){
            this.orderItems = orderItems;
            return this;
        }

        public Builder totalPrice(Long totalPrice){
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder payment(Payment payment){
            this.payment = payment;
            return this;
        }

        public Order build(){
            return Order.builder()
                    .id(id)
                    .orderNumber(orderNumber)
                    .name(name)
                    .buyer(buyer)
                    .orderItems(orderItems)
                    .totalPrice(totalPrice)
                    .payment(payment)
                    .deliveryAddress(deliveryAddress)
                    .primaryOrderStatus(primaryOrderStatus)
                    .build();
        }

    }


}
