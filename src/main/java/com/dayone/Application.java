package com.dayone;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class Application {
    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
        YahooFinanceScraper scraper = new YahooFinanceScraper();
//        var result = scraper.scrap(Company.builder().ticker("O").build());
//        System.out.println(result);
        var res = scraper.scrapCompanyByTicker("MMM");
        System.out.println(res);
    }
}
