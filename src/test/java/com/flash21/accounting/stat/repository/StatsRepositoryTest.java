package com.flash21.accounting.stat.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.fixture.contract.ContractFixture;
import com.flash21.accounting.fixture.contract.DetailContractFixture;
import com.flash21.accounting.fixture.correspondent.CorrespondentFixture;
import com.flash21.accounting.fixture.payment.PaymentFixture;
import com.flash21.accounting.fixture.user.UserFixture;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.repository.OwnerRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class StatsRepositoryTest {

    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private DetailContractRepository detailContractRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CorrespondentRepository correspondentRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private Owner owner;
    private User user;
    private Integer year = 2025;
    private Contract contract;
    private Correspondent correspondent;
    private List<DetailContract> detailContracts;

    /**
     * 특정 유저의 계약서 관련 데이터들을 어떻게 가져와서 YearStats로 저장할 것인가?
     * input: user_id, year
     * 해당 조건에 부합하는 계약서, 계약 내역들을 모두 가져옴
     * 거래처 카테고리로 필터링
     * 지역별 계약서, 계약 내용 파싱
     * 모든 계약서의 개수 카운트: 계약수, 모든 계약 내역의 총금액 카운트: 매출액
     */

    @BeforeEach
    void setUp() {
        owner = ownerRepository.save(OwnerFixture.createDefault());
        user = userRepository.save(UserFixture.createDefault());
        correspondent = correspondentRepository.save(CorrespondentFixture.createWithAllSearchConditions(owner));
        contract = contractRepository.save(ContractFixture.createDefault(user, correspondent));
        detailContracts = new ArrayList<>();
        detailContracts.add(detailContractRepository.save(DetailContractFixture.createWithPriceAndQuantity( contract, 10000, 1)));
        detailContracts.add(detailContractRepository.save(DetailContractFixture.createWithPriceAndQuantity( contract, 20000, 5)));
        detailContracts.add(detailContractRepository.save(DetailContractFixture.createWithPriceAndQuantity( contract, 35000, 3)));

        // 연관관계 설정
        contract.addDetailContract(detailContracts.get(0));
        contract.addDetailContract(detailContracts.get(1));
        contract.addDetailContract(detailContracts.get(2));

        contract = contractRepository.save(contract);
    }

    @DisplayName("YearStats 데이터 생성에 필요한 데이터 가져오기 테스트")
    @Test
    void saveYearStatsData() {
        List<Contract> returns = statsRepository.getContracts(user.getId(), year);
        assertThat(returns).hasSize(1);
        List<DetailContract> detailContractsList = returns.getFirst().getDetailContracts();
        System.out.println("detailContracts: " + returns.getFirst().getDetailContracts());
        assertThat(detailContractsList.size()).isEqualTo(3);
    }

    @DisplayName("YearStats 필요한 데이터들을 조립하여 YearStats 생성하기 테스트")
    @Test
    void createYearStats() {

    }

}