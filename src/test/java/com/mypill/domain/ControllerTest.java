package com.mypill.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.address.controller.AddressController;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.buyer.controller.BuyerController;
import com.mypill.domain.cart.controller.CartController;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.comment.controller.CommentController;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.diary.controller.DiaryController;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.home.controller.HomeController;
import com.mypill.domain.member.controller.MemberController;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.nutrient.service.NutrientService;
import com.mypill.domain.order.controller.OrderController;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.order.service.TossPaymentService;
import com.mypill.domain.post.controller.PostController;
import com.mypill.domain.post.service.PostService;
import com.mypill.domain.product.controller.ProductController;
import com.mypill.domain.product.service.ProductService;
import com.mypill.domain.productlike.service.ProductLikeService;
import com.mypill.domain.seller.controller.SellerController;
import com.mypill.global.AppConfig;
import com.mypill.global.rq.Rq;
import com.mypill.global.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        AddressController.class,
        BuyerController.class,
        CartController.class,
        CommentController.class,
        DiaryController.class,
        HomeController.class,
        MemberController.class,
        OrderController.class,
        ProductController.class

})
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AddressService addressService;

    @MockBean
    protected CartService cartService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected DiaryService diaryService;

    @MockBean
    protected NutrientService nutrientService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected PostService postService;

    @MockBean
    protected ProductService productService;

    @MockBean
    protected ProductLikeService productLikeService;

    @MockBean
    protected Rq rq;

    @MockBean
    protected TossPaymentService tossPaymentService;
}
