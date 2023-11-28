package com.mypill.domain.post.controller;

import com.mypill.common.factory.CommentFactory;
import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.PostFactory;
import com.mypill.domain.ControllerTest;
import com.mypill.domain.comment.dto.response.CommentResponse;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.domain.post.dto.response.PostResponse;
import com.mypill.domain.post.entity.Post;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerTests extends ControllerTest {

    @Test
    @DisplayName("회원은 게시글 목록을 조회할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void showListSuccessTest() throws Exception {
        // GIVEN
        Member member = MemberFactory.member(1L, "testMember");
        List<PostResponse> content = List.of(PostFactory.postResponse(member));
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostResponse> page = new PageImpl<>(content, pageable, content.size());

        given(postService.getPosts(any(), any(), anyInt(), anyInt()))
                .willReturn(page);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/list")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 게시글 작성 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void showCreateFormSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 게시글을 작성할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void createSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember");
        Post testPost = PostFactory.post(testMember.getId(), "post");

        given(rq.getMember()).willReturn(testMember);
        given(postService.create(any(PostRequest.class), any(Member.class)))
                .willReturn(testPost);

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/post/create")
                        .with(csrf())
                        .param("title", "title")
                        .param("content", "content")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("게시글을 조회할 수 있다")
    @WithMockUser
    void showPostSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember");
        PostResponse postResponse = PostFactory.postResponse(testMember);
        CommentResponse commentResponse = CommentFactory.commentResponse(testMember);
        Long postId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(postService.showDetail(anyLong()))
                .willReturn(RsData.successOf(postResponse));
        given(commentService.getCommentsWithMembers(anyLong()))
                .willReturn(List.of(commentResponse));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/detail/{postId}", postId))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("게시글을 자신의 수정 페이지에 접근할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void showUpdateFormSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember");        Post post = PostFactory.post(testMember.getId(), "post");
        Post testPost = PostFactory.post(testMember.getId(), "post");
        Long postId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(postService.beforeUpdate(anyLong(), anyLong()))
                .willReturn(RsData.successOf(testPost));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/update/{postId}", postId))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    @DisplayName("회원은 자신의 게시글을 수정할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void updateSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember");
        Post testPost = PostFactory.post(testMember.getId(), "post");
        Long postId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(postService.update(anyLong(), any(PostRequest.class), anyLong()))
                .willReturn(RsData.successOf(testPost));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/post/update/{postId}", postId)
                        .with(csrf())
                        .param("title", "title")
                        .param("content", "content")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("회원은 자신의 게시글을 삭제할 수 있다")
    @WithMockUser(authorities = "MEMBER")
    void deleteSuccessTest() throws Exception {
        // GIVEN
        Member testMember = MemberFactory.member(1L, "testMember");
        Post testPost = PostFactory.post(testMember.getId(), "post");
        Long postId = 1L;

        given(rq.getMember()).willReturn(testMember);
        given(postService.softDelete(anyLong(), any(Member.class)))
                .willReturn(RsData.successOf(testPost));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/post/delete/{postId}", postId)
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

}
