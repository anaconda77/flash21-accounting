package com.flash21.accounting.contract.entity;

import com.flash21.accounting.correspondent.domain.Correspondent;
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

    @Column(nullable = false, length = 6)
    private String status;

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
    @JoinColumn(name = "director_sign_id")  // 🔹 @Column 제거하고 @JoinColumn 사용
    private Sign directorSign;

    @Column(nullable = false)
    private Integer categoryId;

    // 계약 상대방 (Correspondent 테이블과 ManyToOne 관계 설정)
    @ManyToOne
    @JoinColumn(name = "correspondent_id", nullable = false)
    private Correspondent correspondent;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<DetailContract> detailContracts = new ArrayList<>();

    // 빌더 패턴을 위한 생성자 추가
    @Builder
    public Contract(User admin, Sign headSign, Sign directorSign, String category, String status,
                    String name, LocalDate contractStartDate, LocalDate contractEndDate, LocalDate workEndDate,
                    Integer categoryId, Correspondent correspondent) {
        this.admin = admin;
        this.headSign = headSign; // 🔹 Integer → Sign 변경
        this.directorSign = directorSign; // 🔹 Integer → Sign 변경
        this.category = category;
        this.status = status;
        this.name = name;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.workEndDate = workEndDate;
        this.categoryId = categoryId;
        this.correspondent = correspondent;
    }
}
