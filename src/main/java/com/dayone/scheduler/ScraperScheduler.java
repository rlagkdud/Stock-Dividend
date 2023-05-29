package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableCaching
@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    // 매일 정각에 실행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        log.info("scraping Scheduler is started"); // 스크래핑 스캐쥴러 동작 로그 남김
        // 1. 저장된 회사 목록 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 2. 회사마다 배당금 정보 새로 스크래핑
        for(var company : companies){
            log.info("scraping Scheduler is started -> "+company.getName()); // 어느 회사 정보가 스크래핑 되는지 로그 남김.
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(
                    new Company(company.getName(), company.getTicker()));
            // 3. 스크래핑한 배당금 정보 중 없는 정보는 데이터 베이스에 저장
            scrapedResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e->new DividendEntity(company.getId(),e))
                    // 엘리먼트를 하나씩 디비든 레포지토리에 삽입
                    .forEach(e->{
                        boolean exists = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exists){
                            this.dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });
            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
