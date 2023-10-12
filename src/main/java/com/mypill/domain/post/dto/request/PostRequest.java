package com.mypill.domain.post.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    @NotNull
    @Size(max = 50)
    private String title;
    @NotNull
    @Size(max = 1000)
    private String content;
    private MultipartFile imageFile;
}
