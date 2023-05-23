package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        // 회사의 존재여부 확인 - 존재하면 에러, 아니면 저장
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists){
            throw new RuntimeException("Already exists ticker -> "+ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public List<CompanyEntity> getAllCompany(){
        return this.companyRepository.findAll();
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 가지고 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("Failed to scrap ticker -> " + ticker);
        }
        // 해당 회사가 존재할 경우, 회사의 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        // 회사 저장, 배당금 정보 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                                                    .map(e -> new DividendEntity(companyEntity.getId(), e))
                                                    .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }
}
