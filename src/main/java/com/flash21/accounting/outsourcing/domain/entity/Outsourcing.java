package com.flash21.accounting.outsourcing.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.common.exception.errorcode.OutsourcingErrorCode;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "outsourcing")
public class Outsourcing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outsourcingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correspondent_id", nullable = false)
    private Correspondent correspondent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_contract_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT,
                    foreignKeyDefinition = "FOREIGN KEY (detail_contract_id) REFERENCES detail_contract(detail_contract_id) ON DELETE CASCADE"))
    private DetailContract detailContract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutsourcingStatus status;

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

    public void updateOutsourcing(OutsourcingUpdateRequest updateDto) {
        if(this.status == OutsourcingStatus.CANCELED){
            throw new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_CANCELED_CONTRACT);
        }

        if(updateDto.getStatus() != null){
            OutsourcingStatus newStatus = OutsourcingStatus.fromDisplayName(updateDto.getStatus());
            OutsourcingStatus.validateStatusTransition(this.status, newStatus);
            this.status = newStatus;
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
    }


    // 연관관계 편의 메서드
    public void setDetailContract(DetailContract detailContract) {
        this.detailContract = detailContract;
    }
}
