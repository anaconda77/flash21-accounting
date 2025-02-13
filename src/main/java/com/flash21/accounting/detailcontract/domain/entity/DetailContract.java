package com.flash21.accounting.detailcontract.domain.entity;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.file.domain.AttachmentFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "detail_contract")
public class DetailContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(nullable = false, length = 20)
    private String contractType;

    @Column(nullable = false, length = 10)
    private String contractStatus;

    @Column(nullable = false, length = 20)
    private String largeCategory;

    @Column(nullable = false, length = 20)
    private String smallCategory;

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

    @Column(columnDefinition = "TEXT")
    private String mainContractContent;

    @Column(columnDefinition = "TEXT")
    private String outsourcingContent;

    @Column(nullable = false)
    private LocalDateTime registerDate;

    @Column(nullable = false, length = 20)
    private String lastModifyUser;

    @Column(columnDefinition = "TEXT")
    private String history;

    @OneToMany(mappedBy = "detailContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Outsourcing> outsourcings = new ArrayList<>();

    @OneToMany(mappedBy = "detailContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

//    @OneToMany(mappedBy = "detailContract", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<AttachmentFile> attachmentFiles = new ArrayList<>();


    @Builder
    public DetailContract(Contract contract, String contractType, String contractStatus,
                          String largeCategory, String smallCategory, String content,
                          Integer quantity, Integer unitPrice, Integer supplyPrice,
                          Integer totalPrice, String mainContractContent,
                          String outsourcingContent, String lastModifyUser,
                          String history) {
        this.contract = contract;
        this.contractType = contractType;
        this.contractStatus = contractStatus;
        this.largeCategory = largeCategory;
        this.smallCategory = smallCategory;
        this.content = content;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplyPrice = supplyPrice;
        this.totalPrice = totalPrice;
        this.mainContractContent = mainContractContent;
        this.outsourcingContent = outsourcingContent;
        this.registerDate = LocalDateTime.now();
        this.lastModifyUser = lastModifyUser;
        this.history = history;
    }

    public void update(String contractType, String contractStatus,
                       String largeCategory, String smallCategory, String content,
                       Integer quantity, Integer unitPrice, Integer supplyPrice,
                       Integer totalPrice, String mainContractContent,
                       String outsourcingContent, String lastModifyUser,
                       String history) {
        this.contractType = contractType;
        this.contractStatus = contractStatus;
        this.largeCategory = largeCategory;
        this.smallCategory = smallCategory;
        this.content = content;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplyPrice = supplyPrice;
        this.totalPrice = totalPrice;
        this.mainContractContent = mainContractContent;
        this.outsourcingContent = outsourcingContent;
        this.lastModifyUser = lastModifyUser;
        this.history = history;
    }
}