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
@AllArgsConstructor
@Builder
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

    @Column(nullable = false)
    @Builder.Default
    private boolean hasOutsourcing = false;

    public void updateDetailContract(DetailContractUpdateRequest updateDto) {
        if (this.status == DetailContractStatus.CANCELED) {
            throw new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_CANCELED_CONTRACT);
        }

        if(updateDto.getStatus() != null) {
            DetailContractStatus newStatus = DetailContractStatus.fromDisplayName(updateDto.getStatus());
            DetailContractStatus.validateStatusTransition(this.status, newStatus);
            this.status = newStatus;
        }

        if(updateDto.getDetailContractCategory() != null) {
            this.detailContractCategory = DetailContractCategory.fromDisplayName(updateDto.getDetailContractCategory());
        }
        if(updateDto.getContent() != null) {
            this.content = updateDto.getContent();
        }
        if(updateDto.getQuantity() != null) {
            this.quantity = updateDto.getQuantity();
        }
        if(updateDto.getUnitPrice() != null) {
            this.unitPrice = updateDto.getUnitPrice();
        }
        if(updateDto.getSupplyPrice() != null) {
            this.supplyPrice = updateDto.getSupplyPrice();
        }
        if(updateDto.getTotalPrice() != null) {
            this.totalPrice = updateDto.getTotalPrice();
        }
    }
}