package com.flash21.accounting.detailcontract.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "detail_contract")
public class DetailContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DetailContractCategory detailContractCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DetailContractStatus status;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer supplyPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @OneToOne(mappedBy = "detailContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @Column(nullable = false)
    private boolean hasOutsourcing = false;

    // Outsourcing 연관관계 편의 메서드 추가
    public void setOutsourcing(boolean hasOutsourcing) {
        this.hasOutsourcing = hasOutsourcing;
    }

    public boolean isHasOutsourcing() {
        return hasOutsourcing;
    }

    @Builder
    public DetailContract(Contract contract, DetailContractCategory detailContractCategory, DetailContractStatus status,
                          String content, Integer quantity, Integer unitPrice, Integer supplyPrice,
                          Integer totalPrice, Payment payment, boolean hasOutsourcing) {
        this.contract = contract;
        this.detailContractCategory = detailContractCategory;
        this.status = status;
        this.content = content;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplyPrice = supplyPrice;
        this.totalPrice = totalPrice;
        this.payment = payment;
        this.hasOutsourcing = hasOutsourcing;
    }

    public void updateDetailContract(DetailContractUpdateRequest updateDto) {
        // CANCELED 상태일 경우 모든 수정 불가
        if (this.status == DetailContractStatus.CANCELED) {
            throw new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_CANCELED_CONTRACT);
        }

        // 상태 변경 시 검증
        if(updateDto.getStatus() != null){
            DetailContractStatus newStatus = DetailContractStatus.fromDisplayName(updateDto.getStatus());
            validateStatusTransition(this.status, newStatus);
            this.status = newStatus;
        }

        if(updateDto.getDetailContractCategory() != null){
            this.detailContractCategory = DetailContractCategory.fromDisplayName(updateDto.getDetailContractCategory());
        }
        if(updateDto.getContent() != null){
            this.content = updateDto.getContent();
        }
        if(updateDto.getQuantity() != null){
            this.quantity = updateDto.getQuantity();
        }
        if(updateDto.getUnitPrice() != null){
            this.unitPrice = updateDto.getUnitPrice();
        }
        if(updateDto.getSupplyPrice() != null){
            this.supplyPrice = updateDto.getSupplyPrice();
        }
        if(updateDto.getTotalPrice() != null){
            this.totalPrice = updateDto.getTotalPrice();
        }

        if(updateDto.getPaymentMethod() != null || updateDto.getPaymentCondition() != null){
            this.payment.update(updateDto.getPaymentMethod(), updateDto.getPaymentCondition());
        }

    }

    // 상태 변경 유효성 검사 메서드
    // TEMPORARY → ONGOING → DONE 순서
    // CANCELED로 변경은 언제든 가능
    // CANCELED,DONE 상태면 변경 불가
    private void validateStatusTransition(DetailContractStatus currentStatus, DetailContractStatus newStatus) {
        // CANCELED 상태로의 변경은 항상 가능
        if (newStatus == DetailContractStatus.CANCELED) {
            return;
        }

        // 현재 상태가 CANCELED면 상태 변경 불가
        if (currentStatus == DetailContractStatus.CANCELED) {
            throw new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_CANCELED_CONTRACT);
        }

        // 현재 상태별 가능한 다음 상태 검사
        switch (currentStatus) {
            case TEMPORARY:
                if (newStatus != DetailContractStatus.ONGOING) {
                    throw new AccountingException(DetailContractErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;
            case ONGOING:
                if (newStatus != DetailContractStatus.DONE) {
                    throw new AccountingException(DetailContractErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;
            case DONE:
                throw new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_DONE_CONTRACT);
            default:
                throw new AccountingException(DetailContractErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

}
