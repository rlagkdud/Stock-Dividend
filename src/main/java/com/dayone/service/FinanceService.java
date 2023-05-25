package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = "finance")
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("search company -> " +companyName);

        // 1. 회사명을 기준으로 회사 정보 조회
        Optional<CompanyEntity> byName = companyRepository.findByName(companyName);
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 회사명입니다"));

        // 2. 조회된 회사의 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findByCompanyId(company.getId());

        // 3. 결과 조합 후 반환

        // entity->model로 바꾸기 - 1. for 문 사용
        List<Dividend> dividends = new ArrayList<>();
        for(var de : dividendEntities){
            dividends.add(new Dividend(de.getDividend(), de.getDate()));
        }
//        // entity->model로 바꾸기 - 2. stream 사용
//        List<Dividend> d = dividendEntities.stream()
//                .map(e -> Dividend.builder()
//                        .dividend(e.getDividend())
//                        .date(e.getDate())
//                        .build())
//                .collect(Collectors.toList());


        return new ScrapedResult(new Company(company.getName(), company.getTicker()), dividends);
    }
}
