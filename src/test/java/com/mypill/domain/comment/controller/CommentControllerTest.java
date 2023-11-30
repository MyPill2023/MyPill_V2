package com.mypill.domain.comment.controller;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.PostFactory;
import com.mypill.domain.comment.dto.request.CommentRequest;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    private CommentRequest commentRequest;
    private Member testMember;
    private Post testPost;

    @BeforeEach()
    void beforeEach() {
        commentRequest = new CommentRequest("새 댓글");
        testMember = memberRepository.save(MemberFactory.member("testMember"));
        testPost = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));
    }

    @Test
    @DisplayName("댓글 등록 시 리다이렉션")
    void createTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/create/" + testPost.getId())
                        .with(csrf()) // CSRF 키 생성
                        .param("newContent", commentRequest.getNewContent())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @DisplayName("댓글 수정 시 메서드 체크")
    void updateTest() throws Exception {
        // GIVEN
        Comment comment = commentService.create(commentRequest, testMember, testPost.getId()).getData();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/update/" + comment.getId())
                        .with(csrf()) // CSRF 키 생성
                        .param("newContent", commentRequest.getNewContent())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("update"))
        ;
    }

    @Test
    @DisplayName("댓글 삭제 시 리다이렉션")
    void deleteTest() throws Exception {
        // GIVEN
        Comment comment = commentService.create(commentRequest, testMember, testPost.getId()).getData();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/delete/" + testPost.getId() + "/" + comment.getId())
                        .with(csrf()) // CSRF 키 생성
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
        ;
    }
}