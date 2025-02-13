package com.flash21.accounting.contract.entity;

import com.flash21.accounting.category.domain.Category;
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
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Status status = Status.TEMPORARY; // '임시'로 기본값 설정

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private ProcessStatus processStatus = ProcessStatus.AWAITING_PAYMENT; // '결재진행'으로 기본값 설정

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private LocalDate contractStartDate;

    private LocalDate contractEndDate;
    private LocalDate workEndDate;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @ManyToOne
    @JoinColumn(name = "writer_sign_id")
    private Sign writerSign;

    @ManyToOne
    @JoinColumn(name = "head_sign_id")
    private Sign headSign;

    @ManyToOne
    @JoinColumn(name = "director_sign_id")
    private Sign directorSign;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "correspondent_id", nullable = false)
    private Correspondent correspondent;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<DetailContract> detailContracts = new ArrayList<>();

    @Builder
    public Contract(Long contractId, Status status, ProcessStatus processStatus,
                    String name, LocalDate contractStartDate, LocalDate contractEndDate, LocalDate workEndDate,
                    User admin, Sign writerSign, Sign headSign, Sign directorSign,
                    Category category, Correspondent correspondent, List<DetailContract> detailContracts) {
        this.contractId = contractId;
        this.category = category;
        this.status = status != null ? status : Status.TEMPORARY;
        this.processStatus = processStatus != null ? processStatus : ProcessStatus.AWAITING_PAYMENT;
        this.name = name;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.workEndDate = workEndDate;
        this.admin = admin;
        this.writerSign = writerSign;
        this.headSign = headSign;
        this.directorSign = directorSign;
        this.correspondent = correspondent;
        this.detailContracts = detailContracts != null ? detailContracts : new ArrayList<>();
    }
}
