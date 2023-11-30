package com.mypill.domain.diary.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiaryRequest {
    @NotEmpty(message = "영양제 이름을 입력해주세요.")
    private String name;
    private String time;
}
