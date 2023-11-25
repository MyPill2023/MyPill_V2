package com.mypill.common.factory;

import com.mypill.common.mock.MockPost;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.domain.post.entity.Post;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

public class PostFactory {
    private PostFactory(){}

    public static Post post(String title){
        return createPost(null, title, "content", null);
    }

    public static Post post(Long posterId, String title){
        return createPost(posterId, title, "content", null);
    }

    public static Post post(Long posterId, String title, String content){
        return createPost(posterId, title, content, null);
    }

    public static Post post(String title, LocalDateTime deleteDate){
        return createPost(null, title, "content", deleteDate);
    }

    public static Post createPost(Long posterId, String title, String content, LocalDateTime deleteDate){
        return MockPost.builder()
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
}
