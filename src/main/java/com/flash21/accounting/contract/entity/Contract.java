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
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 받는 생성자 추가
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Method method = Method.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private ProcessStatus processStatus = ProcessStatus.AWAITING_PAYMENT;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private LocalDate contractStartDate;

    private LocalDate contractEndDate;
    private LocalDate workEndDate;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<DetailContract> detailContracts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "correspondent_id", nullable = false)
    private Correspondent correspondent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContractCategory contractCategory = ContractCategory.NONE;

    @Column(columnDefinition = "TEXT")
    private String mainContractContent;

    @Column(nullable = false, updatable = false)
    private LocalDate registerDate;
    @PrePersist
    protected void onCreate() {
        this.registerDate = LocalDate.now(); // 자동으로 오늘 날짜 설정
    }

    @ManyToOne
    @JoinColumn(name = "last_modify_user", nullable = false)
    private User lastModifyUser;


}
