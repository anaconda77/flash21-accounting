package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.Outsourcing;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.OutsourcingRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class DetailContractRepositoryTest {

    @Autowired
    private DetailContractRepository detailContractRepository;

    @Autowired
    private OutsourcingRepository outsourcingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("세부계약서 저장 시 외주/지불정보도 함께 저장되어야 한다")
    void saveDetailContractWithOutsourcingAndPayment() {
        // given
        DetailContract detailContract = createDetailContract();
        Outsourcing outsourcing = createOutsourcing(detailContract);
        Payment payment = createPayment(detailContract);

        detailContract.getOutsourcings().add(outsourcing);
        detailContract.getPayments().add(payment);

        // when
        DetailContract savedContract = detailContractRepository.save(detailContract);

        // then
        assertThat(savedContract.getDetailContractId()).isNotNull();
        assertThat(savedContract.getOutsourcings()).hasSize(1);
        assertThat(savedContract.getPayments()).hasSize(1);
    }

    @Test
    @DisplayName("세부계약서 삭제 시 외주/지불정보도 함께 삭제되어야 한다")
    void deleteDetailContractCascade() {
        // given
        DetailContract detailContract = createDetailContract();
        Outsourcing outsourcing = createOutsourcing(detailContract);
        Payment payment = createPayment(detailContract);

        detailContract.getOutsourcings().add(outsourcing);
        detailContract.getPayments().add(payment);

        DetailContract savedContract = detailContractRepository.save(detailContract);

        // when
        detailContractRepository.delete(savedContract);

        // then
        assertThat(outsourcingRepository.findAll()).isEmpty();
        assertThat(paymentRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("상위계약서 ID로 여러 세부계약서 조회 테스트")
    void findMultipleDetailContractsByContractId() {
        // given
        Long contractId = 1L;
        DetailContract detailContract1 = createDetailContract(contractId, "일반", "웹 개발 A");
        DetailContract detailContract2 = createDetailContract(contractId, "외주", "웹 개발 B");
        detailContractRepository.saveAll(List.of(detailContract1, detailContract2));

        // when
        List<DetailContract> foundContracts = detailContractRepository.findByContractId(contractId);

        // then
        assertThat(foundContracts).hasSize(2);
        assertThat(foundContracts).extracting("content")
                .containsExactlyInAnyOrder("웹 개발 A", "웹 개발 B");
    }

    private DetailContract createDetailContract() {
        return DetailContract.builder()
                .contractId(1L)
                .contractType("일반계약")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("테스트")
                .quantity(1)
                .unitPrice(1000)
                .supplyPrice(1000)
                .totalPrice(1100)
                .lastModifyUser("tester")
                .build();
    }

    private Outsourcing createOutsourcing(DetailContract detailContract) {
        return Outsourcing.builder()
                .detailContract(detailContract)
                .outsourcingName("외주1")
                .content("외주내용")
                .quantity(1)
                .unitPrice(1000)
                .supplyPrice(1000)
                .totalAmount(1100)
                .build();
    }

    private Payment createPayment(DetailContract detailContract) {
        return Payment.builder()
                .detailContract(detailContract)
                .method("카드")
                .condition("선결제")
                .build();
    }

    // 테스트용 DetailContract 생성 메서드 추가
    private DetailContract createDetailContract(Long contractId, String type, String content) {
        return DetailContract.builder()
                .contractId(contractId)
                .contractType(type)
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content(content)
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .lastModifyUser("tester")
                .build();
    }
}