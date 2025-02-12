package com.flash21.accounting.sign.dto;

import lombok.Data;

@Data
public class SignRequestDto {
    private Long userId;
    private String signType;
    private String signImage;
}
