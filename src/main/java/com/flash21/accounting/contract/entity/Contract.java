package com.flash21.accounting.contract.entity;

import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.sign.entity.Sign;
import com.flash21.accounting.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Long contractId;

    @Column(nullable = false, length = 20)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Status status = Status.TEMPORARY; // 기본값 설정


    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private LocalDate contractStartDate;

    private LocalDate contractEndDate;
    private LocalDate workEndDate;

    // 관리자 (User 테이블과 ManyToOne 관계 설정)
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    // 서명 관련 (Sign 테이블과 연관)
    @ManyToOne
    @JoinColumn(name = "head_sign_id")
    private Sign headSign;

    @ManyToOne
    @JoinColumn(name = "director_sign_id")
    private Sign directorSign;

    @Column(nullable = false)
    private Integer categoryId;

    // 계약 상대방 (Correspondent 테이블과 ManyToOne 관계 설정)
    @ManyToOne
    @JoinColumn(name = "correspondent_id", nullable = false)
    private Correspondent correspondent;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<DetailContract> detailContracts = new ArrayList<>();
}
