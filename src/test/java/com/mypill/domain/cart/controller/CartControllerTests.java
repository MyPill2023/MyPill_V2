package com.mypill.domain.cart.controller;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CartControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CartService cartService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;

    private Member testUser1;
    private Member testUserSeller1;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void beforeEachTest() {
        emptyFile = new MockMultipartFile("imageFile", new byte[0]);
        testUser1 = memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "구매자")).getData();
        testUserSeller1 = memberService.join(new JoinRequest("testUserSeller1", "김철수", "1234", "testSeller1@test.com", "판매자")).getData();
    }

    @Test
    @DisplayName("01 장바구니 추가 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void addCartProductSuccessTest() throws Exception {
        // GIVEN
        Product product2 = productService.create(new ProductRequest("테스트 상품2", "테스트 설명2",
                15000L, 100L, asList(3L, 4L), asList(3L, 4L), emptyFile), testUserSeller1).getData();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/cart/add")
                        .with(csrf())
                        .param("productId", String.valueOf(product2.getId()))
                        .param("quantity", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addCartProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/product/detail/**"));
    }

    @Test
    @DisplayName("02 장바구니에 담긴 상품 수량 변경 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void addCartProductFailTest() throws Exception {
        // GIVEN
        Product product1 = productService.create(new ProductRequest("테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L), emptyFile), testUserSeller1).getData();
        CartProduct cartProduct = cartService.addCartProduct(testUser1, new CartProductRequest(product1.getId(), 1L)).getData();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/cart/update")
                        .with(csrf())
                        .param("cartProductId", String.valueOf(cartProduct.getId()))
                        .param("newQuantity", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("updateQuantity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cart**"));
    }

    @Test
    @DisplayName("03 장바구니에 담긴 상품 삭제 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void deleteCartProductSuccessTest() throws Exception {
        // GIVEN
        Product product1 = productService.create(new ProductRequest("테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L), emptyFile), testUserSeller1).getData();
        CartProduct cartProduct = cartService.addCartProduct(testUser1, new CartProductRequest(product1.getId(), 1L)).getData();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/cart/delete")
                        .with(csrf())
                        .param("cartProductId", String.valueOf(cartProduct.getId()))
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("deleteCartProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cart**"));
    }
}
