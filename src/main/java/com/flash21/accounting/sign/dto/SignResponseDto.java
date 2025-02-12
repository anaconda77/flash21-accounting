package com.flash21.accounting.sign.dto;

import com.flash21.accounting.sign.entity.Sign;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignResponseDto {
    private Long signId;
    private Long userId;
    private String signType;
    private String signImage;

    public static SignResponseDto from(Sign sign) {
        return SignResponseDto.builder()
                .signId(sign.getSignId())
                .userId(sign.getUser().getId())
                .signType(sign.getSignType())
                .signImage(sign.getSignImage())
                .build();
    }
}
