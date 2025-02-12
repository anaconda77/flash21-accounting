package com.flash21.accounting.detailcontract.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_contract_id", nullable = false)
    private DetailContract detailContract;

    @Column(nullable = false, length = 20)
    private String method;

    @Column(nullable = false, length = 20)
    private String condition;

    @Builder
    public Payment(DetailContract detailContract, String method, String condition) {
        this.detailContract = detailContract;
        this.method = method;
        this.condition = condition;
    }

    public void update(String method, String condition) {
        this.method = method;
        this.condition = condition;
    }
}