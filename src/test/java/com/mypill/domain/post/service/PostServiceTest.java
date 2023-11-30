package com.mypill.domain.post.service;

import com.mypill.common.factory.MemberFactory;
import com.mypill.common.factory.PostFactory;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.domain.post.dto.response.PostResponse;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    private Member testMember;

    @BeforeEach
    void beforeEach() {
        testMember = memberRepository.save(MemberFactory.member("testMember"));
    }

    @Test
    @DisplayName("게시글 등록 성공")
    void createSuccessTest() {
        // GIVEN
        PostRequest postRequest = PostFactory.mockPostRequest("testPost");

        // WHEN
        Post post = postService.create(postRequest, testMember);

        // THEN
        assertThat(post.getTitle()).isEqualTo(postRequest.getTitle());
    }

    @Test
    @DisplayName("게시글 상세 정보")
    void showDetailSuccessTest() {
        // GIVEN
        Post post = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));

        // WHEN
        RsData showDetailRsData = postService.showDetail(post.getId());

        // THEN
        assertThat(showDetailRsData.getResultCode()).isEqualTo("S-1");
    }

    @Test
    @DisplayName("수정 전 확인 성공")
    void beforeUpdateSuccessTest() {
        // GIVEN
        Post post = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));

        // WHEN
        RsData<Post> updateBeforeRsData =  postService.beforeUpdate(post.getId(), testMember.getId());

        // THEN
        assertThat(updateBeforeRsData.getResultCode()).isEqualTo("S-1");
     }

    @Test
    @DisplayName("수정 전 확인 실패 - 존재하지 않는 게시물")
    void beforeUpdateFailTest_NonExistentPost() {
        // WHEN
        RsData<Post> updateBeforeRsData = postService.beforeUpdate(1L, testMember.getId());

        // THEN
        assertThat(updateBeforeRsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("수정 전 확인 실패 - 작성자 불일치")
    void beforeUpdateTest_NonPoster() {
        // GIVEN
        Post post = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));

        // WHEN
        RsData<Post> updateBeforeRsData = postService.beforeUpdate(post.getId(), testMember.getId()+1);

        // THEN
        assertThat(updateBeforeRsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateSuccessTest() {
        // GIVEN
        Post post = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));
        PostRequest postRequest = PostFactory.mockPostRequest("newTestPost");

        // WHEN
        Post updatedPost = postService.update(post.getId(), postRequest, testMember.getId()).getData();

        // THEN
        assertThat(updatedPost.getTitle()).isEqualTo("newTestPost");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void softDeleteSuccessTest() {
        // GIVEN
        Post post = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));

        // WHEN
        Post deletedPost = postService.softDelete(post.getId(), testMember).getData();

        // THEN
        assertThat(deletedPost.getId()).isEqualTo(post.getId());
        assertThat(deletedPost.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시물")
    void softDeleteFailTest_NonExistentPost() {
        // WHEN
        RsData<Post> deletedPostRsData = postService.softDelete(1L, testMember);

        // THEN
        assertThat(deletedPostRsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 작성자 불일치")
    void softDeleteUpdateTest_NonPoster() {
        // GIVEN
        Member testMember2 = memberRepository.save(MemberFactory.member("testMember2"));
        Post post = postRepository.save(PostFactory.post(testMember.getId(), "testPost"));

        // WHEN
        RsData<Post> deletedPostRsData = postService.softDelete(post.getId(), testMember2);

        // THEN
        assertThat(deletedPostRsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getListSuccessTest() {
        // GIVEN
        postRepository.save(PostFactory.post("testPost1"));
        postRepository.save(PostFactory.post("testPost2"));
        postRepository.save(PostFactory.post("testPost3"));

        // WHEN
        List<Post> posts = postService.getList();

        // THEN
        assertThat(posts).hasSize(3);
    }

    @Test
    @DisplayName("내 게시글 목록 조회")
    void getMyPostsSuccessTest() {
        // GIVEN
        Member testMember2 = memberRepository.save(MemberFactory.member("testMember2"));
        postRepository.save(PostFactory.post(testMember.getId(), "testPost1"));
        postRepository.save(PostFactory.post(testMember.getId(), "testPost2"));
        postRepository.save(PostFactory.post(testMember2.getId(), "testPost3"));

        // WHEN
        List<Post> posts1 = postService.getMyPosts(testMember);
        List<Post> posts2 = postService.getMyPosts(testMember2);

        // THEN
        assertThat(posts1).hasSize(2);
        assertThat(posts2).hasSize(1);
    }


    @Test
    @DisplayName("게시글 제목으로 검색")
    void getPostsTest_searchByTitle() {
        // GIVEN
        postRepository.save(PostFactory.post(testMember.getId(), "testPost"));
        postRepository.save(PostFactory.post(testMember.getId(), "게시글"));

        // WHEN
        Page<PostResponse> postResponse = postService.getPosts("testPost", "title", 0, 10);

        // THEN
        assertThat(postResponse.getTotalElements()).isEqualTo(1);
        assertTrue(postResponse.getContent().get(0).getTitle().contains("testPost"));
    }

    @Test
    @DisplayName("게시글 내용으로 검색")
    void getPostsTest_searchByContent() {
        // GIVEN
        postRepository.save(PostFactory.post(testMember.getId(), "testPost", "testContent"));
        postRepository.save(PostFactory.post(testMember.getId(), "게시글", "내용"));

        // WHEN
        Page<PostResponse> postResponse = postService.getPosts("testContent", "content", 0, 10);

        // THEN
        assertThat(postResponse.getTotalElements()).isEqualTo(1);
        assertTrue(postResponse.getContent().get(0).getContent().contains("testContent"));
    }

    @Test
    @DisplayName("삭제된 게시물 가져오기")
    void getDeletedPostsSuccessTest() {
        // GIVEN
        postRepository.save(PostFactory.post("testPost"));
        postRepository.save(PostFactory.post("deletedPost", LocalDateTime.now()));

        // WHEN
        List<Post> deletedPosts = postService.getDeletedPosts();

        // THEN
        assertThat(deletedPosts).hasSize(1);
    }
}