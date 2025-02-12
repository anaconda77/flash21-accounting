package com.flash21.accounting.detailcontract.dto.response;

import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.Outsourcing;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetDetailContractResponse {
    // 세부계약서 기본 정보
    private Long detailContractId;
    private Long contractId;
    private String contractType;
    private String contractStatus;
    private String largeCategory;
    private String smallCategory;
    private String content;
    private Integer quantity;
    private Integer unitPrice;
    private Integer supplyPrice;
    private Integer totalPrice;
    private String mainContractContent;
    private String outsourcingContent;
    private LocalDateTime registerDate;
    private String lastModifyUser;
    private String history;

    // 외주 정보 목록
    private List<OutsourcingResponse> outsourcings;

    // 지출 정보 목록
    private List<PaymentResponse> payments;

    @Getter
    @Builder
    public static class OutsourcingResponse {
        private Long outsourcingId;
        private String outsourcingName;
        private String content;
        private Integer quantity;
        private Integer unitPrice;
        private Integer supplyPrice;
        private Integer totalAmount;

        public static OutsourcingResponse from(Outsourcing outsourcing) {
            return OutsourcingResponse.builder()
                    .outsourcingId(outsourcing.getOutsourcingId())
                    .outsourcingName(outsourcing.getOutsourcingName())
                    .content(outsourcing.getContent())
                    .quantity(outsourcing.getQuantity())
                    .unitPrice(outsourcing.getUnitPrice())
                    .supplyPrice(outsourcing.getSupplyPrice())
                    .totalAmount(outsourcing.getTotalAmount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PaymentResponse {
        private Long paymentId;
        private String method;
        private String condition;

        public static PaymentResponse from(Payment payment) {
            return PaymentResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .method(payment.getMethod())
                    .condition(payment.getCondition())
                    .build();
        }
    }

    public static GetDetailContractResponse from(DetailContract detailContract) {
        return GetDetailContractResponse.builder()
                .detailContractId(detailContract.getDetailContractId())
                .contractId(detailContract.getContract().getContractId())
                .contractType(detailContract.getContractType())
                .contractStatus(detailContract.getContractStatus())
                .largeCategory(detailContract.getLargeCategory())
                .smallCategory(detailContract.getSmallCategory())
                .content(detailContract.getContent())
                .quantity(detailContract.getQuantity())
                .unitPrice(detailContract.getUnitPrice())
                .supplyPrice(detailContract.getSupplyPrice())
                .totalPrice(detailContract.getTotalPrice())
                .mainContractContent(detailContract.getMainContractContent())
                .outsourcingContent(detailContract.getOutsourcingContent())
                .registerDate(detailContract.getRegisterDate())
                .lastModifyUser(detailContract.getLastModifyUser())
                .history(detailContract.getHistory())
                .outsourcings(detailContract.getOutsourcings().stream()
                        .map(OutsourcingResponse::from)
                        .collect(Collectors.toList()))
                .payments(detailContract.getPayments().stream()
                        .map(PaymentResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}