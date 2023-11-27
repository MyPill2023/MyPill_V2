package com.mypill.domain.comment.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.comment.dto.request.CommentRequest;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerTests extends ControllerTest {

    @Test
    @DisplayName("회원은 댓글을 등록할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void createSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member("testMember");
        Long postId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(commentService.create(any(CommentRequest.class), any(Member.class), anyLong()))
                .willReturn(RsData.successOf(new Comment()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/create/{postId}", postId)
                        .with(csrf())
                        .param("newContent", "newContent")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 자신의 댓글을 수정할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void updateSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member("testMember");
        Long commentId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(commentService.update(any(CommentRequest.class), any(Member.class), anyLong()))
                .willReturn(RsData.successOf(""));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/update/{commentId}", commentId)
                        .with(csrf())
                        .param("newContent", "newContent")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 자신의 댓글을 삭제할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void deleteTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member("testMember");
        Long postId = 1L;
        Long commentId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(commentService.softDelete(any(Member.class), anyLong()))
                .willReturn(RsData.successOf(new Comment()));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/delete/{postId}/{commentId}", postId, commentId)
                        .with(csrf())
                        .param("newContent", "newContent")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }
}
