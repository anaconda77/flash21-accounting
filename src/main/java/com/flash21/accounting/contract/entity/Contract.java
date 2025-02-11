package com.flash21.accounting.contract.entity;

import com.flash21.accounting.user.User;
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

    // 관리자 (User 테이블과 ManyToOne 관계 설정)
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    // 서명 관련 (User 테이블과 연관이므로 이후 수정)
    @Column(name = "head_sign_id")
    private Integer headSignId;

    @Column(name = "director_sign_id")
    private Integer directorSignId;

    @Column(nullable = false)
    private Integer categoryId;

    // 계약 상대방 (User가 아닌 다른 엔티티의 ID를 우선 그대로 저장)
    @Column(nullable = false)
    private Integer correspondentId;

    // **🔹 빌더 패턴을 위한 생성자 추가**
    @Builder
    public Contract(User admin, Integer headSignId, Integer directorSignId, String category, String status,
                    String name, LocalDate contractStartDate, LocalDate contractEndDate, LocalDate workEndDate,
                    Integer categoryId, Integer correspondentId) {
        this.admin = admin;
        this.headSignId = headSignId;
        this.directorSignId = directorSignId;
        this.category = category;
        this.status = status;
        this.name = name;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.workEndDate = workEndDate;
        this.categoryId = categoryId;
        this.correspondentId = correspondentId;
    }
}

