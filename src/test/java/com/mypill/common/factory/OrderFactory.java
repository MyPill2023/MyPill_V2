package com.mypill.common.factory;

import com.mypill.common.mock.MockOrder;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;
import com.mypill.domain.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderFactory {
    private OrderFactory(){}

    public static Order order(Member buyer){
        return createOrder(null, buyer, null);
    }

    public static Order order(Member buyer, Payment payment){
        return createOrder(null, buyer, payment);
    }

    public static Order order(Long id, Member buyer){
        return createOrder(id, buyer, null);
    }

    public static OrderItem orderItem(Product product){
        return createOrderItem(null, product, 1L, OrderStatus.BEFORE);
    }

    public static OrderItem orderItem(Order order, Product product){
        return createOrderItem(order, product, 1L, OrderStatus.BEFORE);
    }

    public static OrderItem orderItem(Order order, Product product, Long quantity){
        return createOrderItem(order, product, quantity, OrderStatus.BEFORE);
    }

    public static OrderItem orderItem(Order order, Product product, OrderStatus orderStatus){
        return createOrderItem(order, product, 1L, orderStatus);
    }

    public static Order addOrderItem(Order order, OrderItem orderItem){
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.add(orderItem);
        return order.toBuilder().orderItems(orderItems).build();
    }

    public static Payment paymentDone(){
        return new Payment("paymentKey", "카드", 24000L, LocalDateTime.now(), "DONE");
    }

    public static Payment paymentCanceled(){
        return new Payment("paymentKey", "카드", 24000L, LocalDateTime.now(), "CANCELED");
    }

    public static PayRequest payRequest(Long orderId, Long amount){
        return new PayRequest("paymentKey", String.valueOf(orderId), amount, "123");
    }

    private static Order createOrder(Long id, Member buyer, Payment payment){
        return MockOrder.builder()
                .id(id)
                .buyer(buyer)
                .orderItems(new ArrayList<>())
                .totalPrice(12000L)
                .payment(payment)
                .build();
    }

    private static OrderItem createOrderItem(Order order, Product product, Long quantity, OrderStatus orderStatus){
        return OrderItem.builder()
                .order(order)
                .product(product)
                .price(product.getPrice())
                .quantity(quantity)
                .status(orderStatus)
                .totalPrice(product.getPrice() * quantity)
                .build();
    }

}
