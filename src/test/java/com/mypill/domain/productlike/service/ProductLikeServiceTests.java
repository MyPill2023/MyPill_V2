package com.mypill.domain.productlike.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.ProductFactory;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.domain.productlike.entity.ProductLike;
import com.mypill.domain.productlike.repository.ProductLikeRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ProductLikeServiceTests {

    @Autowired
    private ProductLikeService productLikeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository;

    @Test
    @DisplayName("상품 좋아요 등록 성공")
    void likeSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
        Product product = productRepository.save(ProductFactory.product("product"));

        // WHEN
        RsData<ProductLike> rsData = productLikeService.like(testBuyer.getId(), product.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
        assertThat(rsData.getData().getMemberId()).isEqualTo(testBuyer.getId());
        assertThat(rsData.getData().getProductId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("상품 좋아요 등록 실패 - 존재하지 않는 상품")
    void likeFailTest_NonExistentProduct() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));

        // WHEN
        RsData<ProductLike> rsData = productLikeService.like(testBuyer.getId(), 1L);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("상품 좋아요 등록 실패 - 이미 좋아요 한 상품")
    void likeFailTest_AlreadyLike() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
        Product product = productRepository.save(ProductFactory.product("product"));
        productLikeRepository.save(new ProductLike(testBuyer.getId(), product.getId()));

        // WHEN
        RsData<ProductLike> rsData = productLikeService.like(testBuyer.getId(), product.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("상품 좋아요 취소 성공")
    void unlikeSuccessTest() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
        Product product = productRepository.save(ProductFactory.product("product"));
        productLikeRepository.save(new ProductLike(testBuyer.getId(), product.getId()));

        // WHEN
        RsData<ProductLike> rsData = productLikeService.unlike(testBuyer.getId(), product.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("S-1");
    }

    @Test
    @DisplayName("상품 좋아요 취소 실패 - 존재하지 않는 상품")
    void unlikeFailTest_NonExistentProduct() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));

        // WHEN
        RsData<ProductLike> rsData = productLikeService.unlike(testBuyer.getId(), 1L);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("상품 좋아요 취소 실패 - 이미 좋아요 취소한 상품")
    void unlikeFailTest_AlreadyUnLike() {
        // GIVEN
        Member testBuyer = memberRepository.save(MemberFactory.member("testBuyer1", Role.BUYER));
        Product product = productRepository.save(ProductFactory.product("product"));

        // WHEN
        RsData<ProductLike> rsData = productLikeService.unlike(testBuyer.getId(), product.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }


}
