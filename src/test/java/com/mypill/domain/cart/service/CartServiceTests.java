package com.mypill.domain.cart.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.ProductFactory;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class CartServiceTests  extends IntegrationTest {
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;

    private Member testBuyer;
    private Product testProduct;

    @BeforeEach
    void beforeEachTest() {
        testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
        testProduct = productRepository.save(ProductFactory.product("testProduct1"));
    }

    @Test
    @DisplayName("장바구니에 가져오기")
    void getOrCreateCartSuccessTest_GetCart() {
        // GIVEN
        cartRepository.save(new Cart(testBuyer, new ArrayList<>()));

        // WHEN
        Cart cart = cartService.getOrCreateCart(testBuyer);

        // THEN
        assertThat(cart).isNotNull();
    }

    @Test
    @DisplayName("장바구니에 생성해서 가져오기")
    void getOrCreateCartSuccessTest_CreateCart() {
        // WHEN
        Cart cart = cartService.getOrCreateCart(testBuyer);

        // THEN
        assertThat(cart).isNotNull();
    }

    @Test
    @DisplayName("장바구니에 추가 성공")
    void addProductSuccessTest() {
        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L));
        CartProduct cartProduct = addRsData.getData();

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getProduct().getId()).isEqualTo(testProduct.getId());
    }

    @Test
    @DisplayName("장바구니에 추가 성공 - 이미 담긴 상품")
    void addProductSuccessTest_AlreadyIncluded() {
        // GIVEN
        cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L));

        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L));
        CartProduct cartProduct = addRsData.getData();

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니에 추가 실패 - 존재하지 않는 상품")
    void addProductFailTest_NonExistentProduct() {
        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testBuyer, new CartProductRequest(0L, 1L));

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("장바구니에 추가 실패 - 선택한 수량이 재고보다 많음")
    void addProductFailTest_InsufficientStock() {
        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), testProduct.getStock()+1));

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("장바구니에 추가 실패 - 장바구니에 추가된 수량이 재고보다 많음")
    void addProductFailTest_InsufficientStockInCart() {
        // GIVEN
        cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L));

        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), testProduct.getStock()));

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("F-3");
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 성공")
    void updateCartProductQuantitySuccessTest() {
        // GIVEN
        CartProduct cartProduct = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L)).getData();

        // WHEN
        RsData<CartProduct> updateRsData = cartService.updateCartProductQuantity(testBuyer, cartProduct.getId(), 3L);
        CartProduct updatedCartProduct = updateRsData.getData();

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("S-1");
        assertThat(updatedCartProduct).isNotNull();
        assertThat(updatedCartProduct.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 실패 - 재고 부족")
    void updateCartProductQuantityFailTest_InsufficientStock() {
        // GIVEN
        CartProduct cartProduct = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L)).getData();

        // WHEN
        RsData<CartProduct> updateRsData = cartService.updateCartProductQuantity(testBuyer, cartProduct.getId(), testProduct.getStock()+1);
        CartProduct updatedCartProduct = updateRsData.getData();

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("F-2");
        assertThat(updatedCartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 실패 - 장바구니에 없는 상품")
    void updateCartProductQuantityFailTest_NonExistentProduct() {
        // WHEN
        RsData<CartProduct> updateRsData = cartService.updateCartProductQuantity(testBuyer, 0L, 3L);
        CartProduct updatedCartProduct = updateRsData.getData();

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("F-1");
        assertThat(updatedCartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 성공")
    void hardDeleteCartProductSuccessTest() {
        // GIVEN
        CartProduct cartProduct = cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L)).getData();

        // WHEN
        RsData<CartProduct> deleteRsData = cartService.hardDeleteCartProduct(testBuyer, cartProduct.getId());
        cartProduct = deleteRsData.getData();

        // THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");
        assertThat(cartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 실패 - 장바구니에 없는 상품")
    void hardDeleteCartProductFailTest_NotIncludedInCart() {
        // WHEN
        RsData<CartProduct> deleteRsData = cartService.hardDeleteCartProduct(testBuyer, testProduct.getId());

        // THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-1");
    }

//    @Test
//    @DisplayName("장바구니에서 상품 전체 삭제 성공")
//    void hardDeleteAllCartProductSuccessTest() {
//        // GIVEN
//        Product testProduct2 = productRepository.save(ProductFactory.product("testProduct2"));
//        cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct.getId(), 1L));
//        cartService.addCartProduct(testBuyer, new CartProductRequest(testProduct2.getId(), 1L));
//
//        // WHEN
//        RsData<Cart> deleteRsData = cartService.hardDeleteAllCartProduct(testBuyer);
//        Cart cart = deleteRsData.getData();
//
//        // THEN
//        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");
//        assertThat(cart.getCartProducts()).isNull();
//    }

}
