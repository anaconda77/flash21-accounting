package com.flash21.accounting.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "카테고리명을 입력해주세요.")
    @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다.")
    String name
) {

}
