package com.mypill.domain.order.service;

import com.mypill.common.factory.*;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.repository.AddressRepository;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.repository.CartProductRepository;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.repository.OrderItemRepository;
import com.mypill.domain.order.repository.OrderRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;

    private Member testBuyer;
    private Member testSeller;

    private CartProduct cartProduct;

    @BeforeEach
    void beforeEachTest() {
        testBuyer = memberRepository.save(MemberFactory.member("testBuyer", Role.BUYER));
        testSeller = memberRepository.save(MemberFactory.member("testSeller", Role.SELLER));
    }

    @Test
    @DisplayName("장바구니에서 전체 상품 주문 생성")
    void createFromCartSuccessTest() {
        // GIVEN
        Product product1 = productRepository.save(ProductFactory.product("product1", testSeller));
        Product product2 = productRepository.save(ProductFactory.product("product2", testSeller));
        Cart cart = cartRepository.save(CartFactory.cart(testBuyer));
        addCartProduct(cart, product1);
        addCartProduct(cart, product2);

        // WHEN
        RsData<Order> rsData = orderService.createFromCart(testBuyer);
        Order order = rsData.getData();

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(order).isNotNull();
        assertThat(order.getPayment()).isNull();
        assertThat(order.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("장바구니에서 선택 상품 주문 생성")
    void createFromSelectedCartProductSuccessTest() {
        // GIVEN
        Product product1 = productRepository.save(ProductFactory.product("product1", testSeller));
        Product product2 = productRepository.save(ProductFactory.product("product2", testSeller));
        Cart cart = cartRepository.save(CartFactory.cart(testBuyer));
        addCartProduct(cart, product1);
        addCartProduct(cart, product2);

        // WHEN
        RsData<Order> rsData = orderService.createFromSelectedCartProduct(testBuyer, new String[]{String.valueOf(cartProduct.getId())});
        Order order = rsData.getData();

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(order).isNotNull();
        assertThat(order.getPayment()).isNull();
        assertThat(order.getOrderItems()).hasSize(1);
    }

    @Test
    @DisplayName("특정 상품 주문 생성 성공")
    void createSingleProductSuccessTest() {
        // GIVEN
        Product product = productRepository.save(ProductFactory.product("testProduct", testSeller));

        // WHEN
        RsData<Order> rsData = orderService.createSingleProduct(testBuyer, product.getId(), 1L);
        Order order = rsData.getData();

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(order).isNotNull();
        assertThat(order.getPayment()).isNull();
        assertThat(order.getOrderItems()).hasSize(1);
    }

    @Test
    @DisplayName("특정 상품 주문 생성 실패 - 존재하지 않는 상품")
    void createSingleProductFailTest_NonExistentProduct() {
        // WHEN
        RsData<Order> rsData = orderService.createSingleProduct(testBuyer, 10L, 1L);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("생성된 주문 폼 가져오기 성공")
    void getOrderFormSuccessTest() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer));

        // WHEN
        RsData<Order> rsData = orderService.getOrderForm(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("생성된 주문 폼 가져오기 실패 - 다른 회원의 주문 (권한 없음)")
    void getOrderFormFailTest_Unauthorized() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));
        Member testBuyer2 = memberRepository.save(MemberFactory.member("testBuyer2", Role.BUYER));

        // WHEN
        RsData<Order> rsData = orderService.getOrderForm(testBuyer2, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }


    @Test
    @DisplayName("생성된 주문 폼 가져오기 실패 - 이미 결제된 주문")
    void getOrderFormFailTest_AlreadyPaidOrder() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));

        // WHEN
        RsData<Order> rsData = orderService.getOrderForm(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-3");
    }

    @Test
    @DisplayName("결제된 주문 상세보기 성공")
    void getOrderDetailsSuccessTest() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));

        // WHEN
        RsData<Order> rsData = orderService.getOrderDetails(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("결제된 주문 상세보기 실패 - 결제되지 않은 주문")
    void getOrderDetailsFailTest_UnpaidOrder() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer));

        // WHEN
        RsData<Order> rsData = orderService.getOrderDetails(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("대표 주문 상태 가져오기 성공")
    void getPrimaryOrderItemStatusSuccessTest() {
        // GIVEN
        Order order = OrderFactory.order(testBuyer, OrderFactory.paymentDone());
        Product product1 = productRepository.save(ProductFactory.product("product1", testSeller));
        Product product2 = productRepository.save(ProductFactory.product("product2", testSeller));
        addOrderItem(order, product1, OrderStatus.ORDERED);
        addOrderItem(order, product2, OrderStatus.DELIVERED);

        // WHEN
        OrderStatus orderStatus = orderService.getPrimaryOrderItemStatus(order);

        // THEN
        assertThat(orderStatus).isEqualTo(OrderStatus.ORDERED);
    }

    @Test
    @DisplayName("주문 상태 개수 가져오기 성공")
    void getOrderStatusCountSuccessTest() {
        // GIVEN
        Order order = OrderFactory.order(testBuyer, OrderFactory.paymentDone());
        Product product1 = productRepository.save(ProductFactory.product("product1", testSeller));
        Product product2 = productRepository.save(ProductFactory.product("product2", testSeller));
        addOrderItem(order, product1, OrderStatus.ORDERED);
        addOrderItem(order, product2, OrderStatus.DELIVERED);

        // WHEN
        Map<OrderStatus, Long> map = orderService.getOrderStatusCount(order.getOrderItems());

        // THEN
        assertThat(map.keySet()).hasSize(2);
        assertThat(map).containsEntry(OrderStatus.ORDERED, 1L);
        assertThat(map).containsEntry(OrderStatus.DELIVERED, 1L);
    }

    @Test
    @DisplayName("주문 취소 가능 여부 조회 - 취소 가능")
    void checkIfOrderCanBeCanceled_CanCanceled() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));

        // WHEN
        RsData<Order> rsData = orderService.checkIfOrderCanBeCanceled(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("주문 취소 가능 여부 조회 - 취소 불가 - 이미 취소된 주문")
    void checkIfOrderCanBeCanceled_AlreadyCanceled() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentCanceled()));

        // WHEN
        RsData<Order> rsData = orderService.checkIfOrderCanBeCanceled(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-3");
    }

    @Test
    @DisplayName("주문 취소 가능 여부 조회 - 취소 불가 - 취소 불가 상태인 상품 존재")
    void checkIfOrderCanBeCanceled_NonCancellableProduct() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));
        Product product1 = productRepository.save(ProductFactory.product("product1", testSeller));
        Product product2 = productRepository.save(ProductFactory.product("product2", testSeller));
        addOrderItem(order, product1, OrderStatus.ORDERED);
        addOrderItem(order, product2, OrderStatus.DELIVERED);


        // WHEN
        RsData<Order> rsData = orderService.checkIfOrderCanBeCanceled(testBuyer, order.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-4");
    }

    @Test
    @DisplayName("주문 결제 가능 여부 조회 - 결제 가능")
    void checkIfOrderCanBePaid_CanBePaid() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer));
        PayRequest payRequest = OrderFactory.payRequest(order.getId(), order.getTotalPrice());


        // WHEN
        RsData<Order> rsData = orderService.checkIfOrderCanBePaid(payRequest);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
    }

    @Test
    @DisplayName("주문 결제 가능 여부 조회 - 결제 불가 - 주문 가격과 결제 가격 불일치")
    void checkIfOrderCanBePaid_UnEqualAmount() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer));
        PayRequest payRequest = OrderFactory.payRequest(order.getId(), order.getTotalPrice() + 1);


        // WHEN
        RsData<Order> rsData = orderService.checkIfOrderCanBePaid(payRequest);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("주문 결제 가능 여부 조회 - 결제 불가 - 주문 수량이 재고보다 많음")
    void checkIfOrderCanBePaid_excessiveQuantity() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));
        Product product = productRepository.save(ProductFactory.product("product", testSeller));
        OrderItem orderItem = orderItemRepository.save(OrderFactory.orderItem(order, product, product.getStock() + 1));
        orderRepository.save(OrderFactory.addOrderItem(order, orderItem));
        PayRequest payRequest = OrderFactory.payRequest(order.getId(), order.getTotalPrice());

        // WHEN
        RsData<Order> rsData = orderService.checkIfOrderCanBePaid(payRequest);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-3");
    }

    @Test
    @DisplayName("주문 결제 완료 후 상태 업데이트")
    void updateOrderAsPaymentDoneTest() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer));
        Address address = addressRepository.save(AddressFactory.address(testBuyer, "address"));

        // WHEN
        orderService.updateOrderAsPaymentDone(order, "orderNumber", address.getId(), OrderFactory.paymentDone());

        // THEN
        assertThat(order.getPayment()).isNotNull();
        assertThat(order.getDeliveryAddress()).isNotNull();
        assertThat(order.getPrimaryOrderStatus()).isEqualTo(OrderStatus.ORDERED);
    }

    @Test
    @DisplayName("주문 취소 후 상태 업데이트")
    void updateOrderAsCancelTest() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));

        // WHEN
        orderService.updateOrderAsCancel(order, LocalDateTime.now(), "CANCELED");

        // THEN
        assertThat(order.getPrimaryOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("주문 상품 상태 변경 성공")
    void updateOrderItemStatusSuccessTest() {
        // GIVEN
        Order order = orderRepository.save(OrderFactory.order(testBuyer, OrderFactory.paymentDone()));
        Product product = productRepository.save(ProductFactory.product("product", testSeller));
        OrderItem orderItem = orderItemRepository.save(OrderFactory.orderItem(order, product));
        orderRepository.save(OrderFactory.addOrderItem(order, orderItem));
        String newStatus = "배송 완료";

        // WHEN
        RsData<OrderItem> rsData = orderService.updateOrderItemStatus(orderItem.getId(), newStatus);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getStatus()).isEqualTo(OrderStatus.findByValue(newStatus));
    }

    public void addCartProduct(Cart cart, Product product){
        cartProduct = cartProductRepository.save(CartFactory.cartProduct(cart, product));
        cartRepository.save(CartFactory.addCartProduct(cart, cartProduct));
    }

    public void addOrderItem(Order order, Product product, OrderStatus orderStatus){
        OrderItem orderItem = orderItemRepository.save(OrderFactory.orderItem(order, product, orderStatus));
        orderRepository.save(OrderFactory.addOrderItem(order, orderItem));
    }
}
