package com.flash21.accounting.contract.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractId;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 6)
    private String status;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private LocalDate contractStartDate;

    private LocalDate contractEndDate;
    private LocalDate workEndDate;

    // FK 대신 숫자로 우선 저장
    @Column(nullable = false)
    private Integer adminId;

    @Column
    private Integer headSignId;

    @Column
    private Integer directorSignId;

    @Column(nullable = false)
    private Integer categoryId;

    @Column(nullable = false)
    private Integer correspondentId;
}
