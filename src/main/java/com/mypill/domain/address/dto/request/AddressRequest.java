package com.mypill.domain.address.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String name;
    @NotBlank(message = "수령인 이름을 입력해주세요.")
    private String receiverName;
    @NotBlank(message = "배송지 주소를 입력해주세요.")
    private String address;
    @NotBlank(message = "상세주소를 입력해주세요.")
    private String detailAddress;
    @NotBlank(message = "우편번호는 필수입니다")
    private String postCode;
    @NotBlank(message = "수령인 연락처는 필수입니다")
    private String phoneNumber;
    @JsonProperty("isDefault")
    private boolean isDefault;
}