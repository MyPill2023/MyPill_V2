package com.mypill.common.mock;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.image.entity.Image;
import com.mypill.domain.post.entity.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockPost {
    private MockPost() {}

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private Long id;
        private Long posterId;
        private String title;
        private String content;
        private List<Comment> comments = new ArrayList<>();
        private Image image;
        private LocalDateTime deleteDate;

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder posterId(Long posterId){
            this.posterId = posterId;
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder image(Image image){
            this.image = image;
            return this;
        }

        public Builder deleteDate(LocalDateTime deleteDate){
            this.deleteDate = deleteDate;
            return this;
        }

        public Post build() {
            return Post.builder()
                    .id(id)
                    .posterId(posterId)
                    .title(title)
                    .content(content)
                    .comments(comments)
                    .image(image)
                    .deleteDate(deleteDate)
                    .build();
        }
    }

}
