package com.flash21.accounting.detailcontract.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateDetailContractRequest {
    @NotBlank(message = "계약 유형은 필수입니다.")
    private String contractType;

    @NotBlank(message = "계약 상태는 필수입니다.")
    private String contractStatus;

    @NotBlank(message = "대분류는 필수입니다.")
    private String largeCategory;

    @NotBlank(message = "소분류는 필수입니다.")
    private String smallCategory;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 0보다 커야 합니다.")
    private Integer quantity;

    @NotNull(message = "단가는 필수입니다.")
    @Positive(message = "단가는 0보다 커야 합니다.")
    private Integer unitPrice;

    @NotNull(message = "공급가액은 필수입니다.")
    @Positive(message = "공급가액은 0보다 커야 합니다.")
    private Integer supplyPrice;

    @NotNull(message = "합계금액은 필수입니다.")
    @Positive(message = "합계금액은 0보다 커야 합니다.")
    private Integer totalPrice;

    private String mainContractContent;
    private String outsourcingContent;

    @NotBlank(message = "최종 수정자는 필수입니다.")
    private String lastModifyUser;
    private String history;

    private List<Outsourcing> outsourcings;
    private List<Payment> payments;

    @Getter
    @NoArgsConstructor
    public static class Outsourcing {
        private String outsourcingName;
        private String content;
        private Integer quantity;
        private Integer unitPrice;
        private Integer supplyPrice;
        private Integer totalAmount;

        @Builder
        public Outsourcing(String outsourcingName, String content, Integer quantity,
                           Integer unitPrice, Integer supplyPrice, Integer totalAmount) {
            this.outsourcingName = outsourcingName;
            this.content = content;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.supplyPrice = supplyPrice;
            this.totalAmount = totalAmount;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Payment {
        private String method;
        private String condition;

        @Builder
        public Payment(String method, String condition) {
            this.method = method;
            this.condition = condition;
        }
    }

    @Builder
    public UpdateDetailContractRequest(String contractType, String contractStatus,
                                       String largeCategory, String smallCategory, String content,
                                       Integer quantity, Integer unitPrice, Integer supplyPrice,
                                       Integer totalPrice, String mainContractContent,
                                       String outsourcingContent, String lastModifyUser,
                                       String history, List<Outsourcing> outsourcings,
                                       List<Payment> payments) {
        this.contractType = contractType;
        this.contractStatus = contractStatus;
        this.largeCategory = largeCategory;
        this.smallCategory = smallCategory;
        this.content = content;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplyPrice = supplyPrice;
        this.totalPrice = totalPrice;
        this.mainContractContent = mainContractContent;
        this.outsourcingContent = outsourcingContent;
        this.lastModifyUser = lastModifyUser;
        this.history = history;
        this.outsourcings = outsourcings;
        this.payments = payments;
    }
}