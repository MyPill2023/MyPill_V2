package com.mypill.domain.notification.service;

import com.mypill.common.factory.*;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.notification.repository.NotificationRepository;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.repository.OrderItemRepository;
import com.mypill.domain.order.repository.OrderRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationServiceTests extends IntegrationTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("주문 품목 상태 변경 후 알림 생성")
    void createFromCartSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Member testSeller = memberRepository.save(MemberFactory.member("testSeller", Role.SELLER));
        Product product = productRepository.save(ProductFactory.product("product", testSeller));
        OrderItem orderItem = orderItemRepository.save(OrderFactory.orderItem(product));
        OrderStatus newStatus = OrderStatus.ORDERED;

        // WHEN
        Notification notification = notificationService.whenAfterOrderStatusUpdate(testBuyer, orderItem, newStatus);

        // THEN
        assertThat(notification.getMember().getId()).isEqualTo(testBuyer.getId());
        assertThat(notification.getTypeCode()).isEqualTo(NotificationTypeCode.OrderStatus);
    }

    @Test
    @DisplayName("주문 결제 완료 후 알림 생성")
    void whenAfterOrderPaymentSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));

        // WHEN
        Notification notification = notificationService.whenAfterOrderPayment(testBuyer, order);

        // THEN
        assertThat(notification.getMember().getId()).isEqualTo(testBuyer.getId());
        assertThat(notification.getTypeCode()).isEqualTo(NotificationTypeCode.OrderPayment);
    }

    @Test
    @DisplayName("주문 상태 변경 후 알림 생성")
    void whenAfterOrderChangedSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));

        // WHEN
        Notification notification = notificationService.whenAfterOrderChanged(testBuyer, order, OrderStatus.CANCELED);

        // THEN
        assertThat(notification.getMember().getId()).isEqualTo(testBuyer.getId());
        assertThat(notification.getTypeCode()).isEqualTo(NotificationTypeCode.OrderCanceled);
    }

    @Test
    @DisplayName("주문 취소 후 알림 생성")
    void whenAfterOrderCanceledSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentCanceled()));

        // WHEN
        Notification notification = notificationService.whenAfterOrderCanceled(testBuyer, order);

        // THEN
        assertThat(notification.getMember().getId()).isEqualTo(testBuyer.getId());
        assertThat(notification.getTypeCode()).isEqualTo(NotificationTypeCode.OrderCanceled);
    }

    @Test
    @DisplayName("복용 스케줄 알림 생성")
    void whenBeforeDiaryCheckSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Diary diary = DiaryFactory.diary(testBuyer);

        // WHEN
        Notification notification = notificationService.whenBeforeDiaryCheck(diary);

        // THEN
        assertThat(notification.getMember().getId()).isEqualTo(testBuyer.getId());
        assertThat(notification.getTypeCode()).isEqualTo(NotificationTypeCode.Record);
    }

    @Test
    @DisplayName("알림 읽음 처리 성공")
    void markAsReadSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));
        Notification notification = notificationRepository.save(NotificationFactory.orderPayment(testBuyer, order));

        // WHEN
        RsData<Notification> rsData = notificationService.markAsRead(testBuyer, notification.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getReadDate()).isNotNull();
    }

    @Test
    @DisplayName("알림 읽음 처리 실패 - 존재하지 않는 알림")
    void markAsReadFailTest_NonExistentNotification() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));

        // WHEN
        RsData<Notification> rsData = notificationService.markAsRead(testBuyer, 1L);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("알림 읽음 처리 실패 - 다른 사람의 알림(권한 없음)")
    void markAsReadFailTest_unauthorized() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        Member testBuyer2 = memberRepository.save(MemberFactory.member("testBuyer2", Role.BUYER));
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));
        Notification notification = notificationRepository.save(NotificationFactory.orderPayment(testBuyer, order));

        // WHEN
        RsData<Notification> rsData = notificationService.markAsRead(testBuyer2, notification.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }
}
