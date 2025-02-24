package com.flash21.accounting.stat.repository;

import static org.assertj.core.api.Assertions.*;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.correspondent.domain.Region;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.fixture.contract.ContractFixture;
import com.flash21.accounting.fixture.contract.DetailContractFixture;
import com.flash21.accounting.fixture.correspondent.CorrespondentFixture;
import com.flash21.accounting.fixture.user.UserFixture;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.repository.OwnerRepository;
import com.flash21.accounting.stat.domain.YearStats;
import com.flash21.accounting.stat.domain.YearStatsContent;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Transactional
@Testcontainers
@ActiveProfiles("testcontainers")
class StatsRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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

    private Owner owner;
    private User user;
    private Integer year = 2025;
    private Contract contract;
    private Correspondent correspondent;
    private List<DetailContract> detailContracts;
    private Region region = Region.DAEGU;
    /**
     * 특정 유저의 계약서 관련 데이터들을 어떻게 가져와서 YearStats로 저장할 것인가? input: user_id, year 해당 조건에 부합하는 계약서, 계약
     * 내역들을 모두 가져옴 거래처 카테고리로 필터링 지역별 계약서, 계약 내용 파싱 모든 계약서의 개수 카운트: 계약수, 모든 계약 내역의 총금액 카운트: 매출액
     */

    @BeforeEach
    void setUp() {
        owner = ownerRepository.save(OwnerFixture.createDefault());
        user = userRepository.save(UserFixture.createDefault());
        correspondent = correspondentRepository.save(
            CorrespondentFixture.createWithAllSearchConditions(owner, region));
        contract = contractRepository.save(ContractFixture.createDefault(user, correspondent));
        detailContracts = new ArrayList<>();
        detailContracts.add(detailContractRepository.save(
            DetailContractFixture.createWithPriceAndQuantity(contract, 10000, 1)));
        detailContracts.add(detailContractRepository.save(
            DetailContractFixture.createWithPriceAndQuantity(contract, 20000, 5)));
        detailContracts.add(detailContractRepository.save(
            DetailContractFixture.createWithPriceAndQuantity(contract, 35000, 3)));

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

    /**
     * YearStats가 존재하면 update, 존재하지 않으면 create
     */

    @DisplayName("YearStats 필요한 데이터들을 조립하여 YearStats 생성하기 테스트")
    @Test
    void createYearStats() {

        CorrespondentCategory requestCategory = correspondent.getCorrespondentCategory();
        List<Contract> returns = statsRepository.getContracts(user.getId(), year).stream()
            .filter(c -> c.getCorrespondent().getCorrespondentCategory() == requestCategory).toList();
        Map<String, YearStatsContent> yearStatsContentMap = new HashMap<>();
        Arrays.stream(Region.values())
            .forEach(region -> yearStatsContentMap.put(region.toString(),
                new YearStatsContent(region.toString(), 0, 0L)));

        // yearStats의 content를 각 지역(row) 별로 생성, col에 들어갈 값들을 계산하여 저장
        returns.forEach(c -> {
                YearStatsContent yearStatsContent = yearStatsContentMap.get(
                    c.getCorrespondent().getRegion().toString());
                yearStatsContent.updateCount();
                Long sumsPrice = c.getDetailContracts().stream()
                    .mapToLong(DetailContract::getTotalPrice)
                    .sum();
                yearStatsContent.updateSumsPrice(sumsPrice);
            });

        // list로 변환
        List<YearStatsContent> contents = yearStatsContentMap.values().stream().toList();

        // 이미 db에 해당 user_id, 거래처 카테고리, 연도로 계산한 데이터가 있으면 해당 엔티티를 갱신, 없다면 새로운 엔티티 생성 및 저장
        statsRepository.findByUserId(user.getId())
            .stream()
            .filter(ys -> Objects.equals(ys.getYearNumber(), year) && ys.getCategory() == requestCategory)
            .findFirst()
            .map(ys -> {
                ys.updateContent(contents);
                return ys;  // 기존 데이터 업데이트 후 반환
            })
            .orElseGet(() -> statsRepository.save(
                new YearStats(null, year, requestCategory, user.getId(), contents)
            ));

        YearStats yearStats = statsRepository.findById(1L).get();
        assertThat(yearStats).isNotNull();
        assertThat(yearStats.getYearNumber()).isEqualTo(year);
        assertThat(yearStats.getCategory()).isEqualTo(requestCategory);
        assertThat(yearStats.getUserId()).isEqualTo(user.getId());
        assertThat(yearStats.getContent()).isEqualTo(contents);
        System.out.println(yearStats.getContent());
    }

}