package com.mypill.common.factory;

import com.mypill.common.mock.MockPost;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.domain.post.dto.response.PostResponse;
import com.mypill.domain.post.entity.Post;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

public class PostFactory {
    private PostFactory(){}

    public static Post post(String title){
        return createPost(null, null, title, "content", null);
    }

    public static Post post(Long posterId, String title){
        return createPost(null, posterId, title, "content", null);
    }

    public static Post post(Long id, Long posterId, String title){
        return createPost(id, posterId, title, "content", null);
    }

    public static Post post(Long posterId, String title, String content){
        return createPost(null, posterId, title, content, null);
    }

    public static Post post(String title, LocalDateTime deleteDate){
        return createPost(null, null, title, "content", deleteDate);
    }

    public static Post createPost(Long id, Long posterId, String title, String content, LocalDateTime deleteDate){
        return MockPost.builder()
                .id(id)
                .posterId(posterId)
                .title(title)
                .content(content)
                .deleteDate(deleteDate)
                .build();
    }

    public static PostRequest mockPostRequest(String title){
        return new PostRequest(
                title,
                "content",
                new MockMultipartFile("image", new byte[0])
        );
    }

    public static PostResponse postResponse(Member member){
        Post post = post(member.getId(), "post");
        return PostResponse.of(post, member);
    }

}
