package com.mypill.domain.product.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.ProductFactory;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member testSeller1;

    @BeforeEach
    void beforeEachTest() {
        testSeller1 = memberRepository.save(MemberFactory.member("testSeller1", Role.SELLER));
    }

    @AfterEach
    void afterEachTest() {
        productRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 등록")
    @Order(1)
    void createSuccessTests() {
        // WHEN
        Product testProduct = productService.create(ProductFactory.mockProductRequest("testProduct"), testSeller1).getData();

        // THEN
        assertThat(testProduct).isNotNull();
        assertThat(testProduct.getName()).isEqualTo("testProduct");
    }

    @Test
    @DisplayName("상품 정보 가져오기")
    @Order(2)
    void getSuccessTests() {
        // GIVEN
        Product testProduct = productRepository.save(ProductFactory.product("testProduct", testSeller1));

        // WHEN
        RsData<Product> getRsData = productService.get(testProduct.getId());

        // THEN
        assertThat(getRsData.getResultCode()).isEqualTo("S-1");
        assertThat(getRsData.getData().getName()).isEqualTo("testProduct");
        assertThat(getRsData.getData().getSeller().getId()).isEqualTo(testSeller1.getId());
    }

    @Test
    @DisplayName("상품 수정 - 성공")
    @Order(3)
    void updateSuccessTests() {
        // GIVEN
        Product testProduct = productRepository.save(ProductFactory.product("testProduct", testSeller1));
        ProductRequest request = ProductFactory.mockProductRequest("newProduct");

        // WHEN
        RsData<Product> updateRsData = productService.update(testSeller1, testProduct.getId(), request);
        Product updateProduct = updateRsData.getData();

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("S-1");
        assertThat(updateProduct.getName()).isEqualTo("newProduct");
    }

    @Test
    @DisplayName("상품 수정 - 권한 없음 실패")
    @Order(4)
    void updateFailTests() {
        // GIVEN
        Member testSeller2 = memberRepository.save(MemberFactory.member("testSeller2", Role.SELLER));
        Product testProduct = productRepository.save(ProductFactory.product("testProduct", testSeller1));
        ProductRequest request = ProductFactory.mockProductRequest("newProduct");

        // WHEN
        RsData<Product> updateRsData = productService.update(testSeller2, testProduct.getId(), request);

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("상품 삭제 - 성공")
    @Order(5)
    void deleteSuccessTests() {
        // GIVEN
        Product testProduct = productRepository.save(ProductFactory.product("testProduct", testSeller1));

        // WHEN
        RsData<Product> deleteRsData = productService.softDelete(testSeller1, testProduct.getId());
        Product deletedProduct = deleteRsData.getData();

        // THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("상품 삭제 - 권한 없음 실패")
    @Order(6)
    void deleteFailTests() {
        // GIVEN
        Member testSeller2 = memberRepository.save(MemberFactory.member("testSeller2", Role.SELLER));
        Product testProduct = productRepository.save(ProductFactory.product("testProduct", testSeller1));

        //WHEN
        RsData<Product> deleteRsData = productService.softDelete(testSeller2, testProduct.getId());
        Product deletedProduct = productRepository.findById(testProduct.getId()).orElse(null);

        //THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-2");
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getDeleteDate()).isNull();
    }


    @Test
    @DisplayName("주문에 의한 재고 업데이트 동시성 이슈")
    @Order(7)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testUpdateStockAndSalesByOrderSuccess() throws InterruptedException{
        // GIVEN
        Product testProduct = productRepository.save(ProductFactory.product("testProduct", testSeller1));

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // ThreadPool 구성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 작업이 완료될 때까지 대기

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                        try {
                            productService.updateStockAndSalesByOrder(testProduct.getId(), 1L);
                        }
                        finally {
                            latch.countDown();
                        }
                    }
            );
        }
        latch.await();

        Product newProduct = productService.findById(testProduct.getId()).orElse(null);
        assertThat(newProduct).isNotNull();
        assertThat(newProduct.getStock()).isZero();
        assertThat(newProduct.getSales()).isEqualTo(100L);
    }

}

