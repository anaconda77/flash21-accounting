package com.flash21.accounting.category.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "카테고리명을 입력해주세요.")
    String name
) {

}
