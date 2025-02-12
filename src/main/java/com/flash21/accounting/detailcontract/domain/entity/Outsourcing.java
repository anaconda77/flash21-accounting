package com.flash21.accounting.detailcontract.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outsourcing")
public class Outsourcing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outsourcingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_contract_id", nullable = false)
    private DetailContract detailContract;

    @Column(nullable = false, length = 50)
    private String outsourcingName;

    @Column(length = 255)
    private String content;

    private Integer quantity;
    private Integer unitPrice;
    private Integer supplyPrice;
    private Integer totalAmount;

    @Builder
    public Outsourcing(DetailContract detailContract, String outsourcingName,
                       String content, Integer quantity, Integer unitPrice,
                       Integer supplyPrice, Integer totalAmount) {
        this.detailContract = detailContract;
        this.outsourcingName = outsourcingName;
        this.content = content;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplyPrice = supplyPrice;
        this.totalAmount = totalAmount;
    }

    public void update(String outsourcingName, String content,
                       Integer quantity, Integer unitPrice,
                       Integer supplyPrice, Integer totalAmount) {
        this.outsourcingName = outsourcingName;
        this.content = content;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplyPrice = supplyPrice;
        this.totalAmount = totalAmount;
    }
}
