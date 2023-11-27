package com.mypill.domain.home.controller;

import com.mypill.common.fixture.TProduct;
import com.mypill.domain.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HomeControllerTest extends ControllerTest {

    @Test
    @DisplayName("메인페이지에 접근할 수 있다")
    @WithMockUser
    void showMainSuccessTest() throws Exception {
        // GIVEN
        given(productService.getTop5ProductsBySales())
                .willReturn(List.of(TProduct.PRODUCT_1.getProduct()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }
}