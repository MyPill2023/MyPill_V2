package com.mypill.common.factory;

import com.mypill.domain.comment.dto.response.CommentResponse;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;

public class CommentFactory {

    private CommentFactory(){}

    public static CommentResponse commentResponse(Member member){
        Post post = PostFactory.post(1L, member.getId(), "title");
        Comment comment = new Comment(post, 1L, "content");
        return createResponse(comment, member);
    }

    public static CommentResponse createResponse(Comment comment, Member member){
        return CommentResponse.of(comment, member);
    }
}
