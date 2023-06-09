package com.dayone.scraper;

import com.dayone.model.constants.Month;
import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60 * 60 * 24 -> 1일


    // 회사의 배당금 정보 스크래핑 해오기
    @Override
    public ScrapedResult scrap(Company company) {

        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; // 현재시간을 초로 받아

            String url = String.format(STATISTICS_URL, company.getTicker(),START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0); // table 전체

            Element tbody = tableEle.children().get(1);// get tbody
            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] txtArr = txt.split(" ");
                int month = Month.strToNumber(txtArr[0]);
                int day = Integer.parseInt(txtArr[1].split(",")[0]);
                int year = Integer.parseInt(txtArr[2]);
                String dividend = txtArr[3];

                if (month <= 0) {
                    throw new RuntimeException("Unexpected Month enum value ->" + txtArr[0]);
                }

                dividends.add(new Dividend(dividend, LocalDateTime.of(year, month, day, 0, 0)));
            }
            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            // TODO: 정상적인 스크래핑이 되지 않았을 때
            throw new RuntimeException(e);
        }
        return scrapResult;
    }

    // 회사 메타정보 스크래핑 해오기
    @Override
    public Company scrapCompanyByTicker(String ticker){

        String url = String.format(SUMMARY_URL, ticker,ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();
            return new Company(title, ticker);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
