package com.mypill.domain.comment.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.PostFactory;
import com.mypill.domain.IntegrationTest;
import com.mypill.domain.comment.dto.request.CommentRequest;
import com.mypill.domain.comment.dto.response.CommentResponse;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.repository.CommentRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentServiceTest extends IntegrationTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    private Member testMember;
    private Post testPost;

    @BeforeEach()
    void beforeEach() {
        testMember = memberRepository.save(MemberFactory.member("testMember"));
        testPost = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 작성 성공")
    void createSuccessTest() {
        // GIVEN
        CommentRequest request = new CommentRequest("comment");

        // WHEN
        Comment comment = commentService.create(request, testMember, testPost.getId()).getData();

        // THEN
        assertThat(comment.getContent()).isEqualTo("comment");
    }

    @Test
    @Transactional
    @DisplayName("게시글에 댓글 목록 조회")
    void getCommentsWithMembersSuccessTest() {
        // GIVEN
        commentRepository.save(new Comment(testPost, testMember.getId(), "comment1"));
        commentRepository.save(new Comment(testPost, testMember.getId(), "comment2"));
        commentRepository.save(new Comment(testPost, testMember.getId(), "comment3"));

        // WHEN
        List<CommentResponse> commentResponses = commentService.getCommentsWithMembers(testPost.getId());

        // THEN
        assertThat(commentResponses).hasSize(3);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateSuccessTest() {
        // GIVEN
        Comment comment = commentRepository.save(new Comment(testPost, testMember.getId(), "comment"));
        CommentRequest request = new CommentRequest("updatedComment");

        // WHEN
        String updatedComment = commentService.update(request, testMember, comment.getId()).getData();

        // THEN
        assertThat(updatedComment).isEqualTo("updatedComment");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    void updateFailTest_NonExistentComment() {
        // GIVEN
        CommentRequest request = new CommentRequest("updatedComment");

        // WHEN
        RsData rsData = commentService.update(request, testMember, 1L);

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자 불일치")
    void updateFailTest_NonCommenter() {
        // GIVEN
        Member testMember2 = MemberFactory.member("testMember2");
        Comment comment = commentRepository.save(new Comment(testPost, testMember.getId(), "comment"));
        CommentRequest request = new CommentRequest("updatedComment");

        // WHEN
        RsData rsData = commentService.update(request, testMember2, comment.getId());

        // THEN
        assertThat(rsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteSuccessTest() {
        // GIVEN
        Comment comment = commentRepository.save(new Comment(testPost, testMember.getId(), "comment"));

        // WHEN
        Comment deletedComment = commentService.softDelete(testMember, comment.getId()).getData();

        // THEN
        assertThat(deletedComment.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("내 댓글 목록 보기")
    void getMyCommentsSuccessTest() {
        // GIVEN
        Member testMember2 = MemberFactory.member("testMember2");
        commentRepository.save(new Comment(testPost, testMember.getId(), "comment1"));
        commentRepository.save(new Comment(testPost, testMember.getId(), "comment2"));
        commentRepository.save(new Comment(testPost, testMember2.getId(), "comment3"));

        // WHEN
        List<Comment> comments1 = commentService.getMyComments(testMember);
        List<Comment> comments2 = commentService.getMyComments(testMember2);

        // THEN
        assertThat(comments1).hasSize(2);
        assertThat(comments2).hasSize(1);
    }
}