package com.ecfx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;

import com.ecfx.model.CaptchaResponse;
import com.ecfx.model.PriceLog;
import com.ecfx.model.TrackerConfiguration;
import com.ecfx.service.CaptchaService;
import com.ecfx.service.NotificationService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PriceTrackerApp {

    private static final String AMAZON_IPADTEN_URL = "https://www.amazon.com/Apple-2022-10-9-inch-iPad-Wi-Fi/dp/B0BJLXMVMV/?th=1";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";
    private static final String PRODUCT_PAGE_PATH = "src\\main\\resources\\ipad_product_page.html";
    private static final String PRICE_LOG_PATH = "src\\main\\resources\\log\\ipad_price_log.txt";
    private static final String NOTIFICATION_LOG_PATH = "src\\main\\resources\\log\\notification_log.txt";

    private static Scanner sc = new Scanner(System.in);
    
    //corePriceDisplay_desktop will give you the div that holds the pricing info
    //a-price-whole is the text of price

    public static void main(String[] args) {
        try {
            TrackerConfiguration config = getTrackerConfiguration();
            while (true) {
                PriceLog previousPriceLog = getPreviousPrice(); // Get last straped price from log file
                PriceLog currentPriceLog = getCurrentPrice(config); // Get current price from Amazon product page

                if (previousPriceLog.compareTo(currentPriceLog) > 0) {
                    sendNotification(currentPriceLog);
                }

                TimeUnit.HOURS.sleep(config.getTimeBetweenPoll());
            }

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static PriceLog getPreviousPrice() {
        File priceLog = new File(PRICE_LOG_PATH);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(priceLog));
            String line = reader.readLine();
            String latestEntry = null;
            while (line != null) {
                latestEntry = line;
                line = reader.readLine();
            }
            reader.close();
            if (latestEntry != null) {
                String[] splitEntries = latestEntry.split(" - ");

                if (splitEntries.length == 2) {
                    // return {price, timestamp}
                    return new PriceLog(new BigDecimal(splitEntries[1].trim()).setScale(2), splitEntries[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PriceLog(new BigDecimal(0.00).setScale(2), "");
    }

    private static TrackerConfiguration getTrackerConfiguration() throws IOException{
        TrackerConfiguration config = new TrackerConfiguration();
        System.out.println("Are we scraping in the test environment? " + 
        "Please enter the number corresponding to your answer." + 
        "\n1. Yes\n2. No");
        
        int isTest = Integer.parseInt(sc.nextLine());
        if (isTest == 1) {
            config.setTestConfiguration(true);
        } else {
            config.setTestConfiguration(false);
        }

        System.out.println("Enter the time between poll in hours:");
        config.setTimeBetweenPoll(Integer.parseInt(sc.nextLine()));
    
        return config;
    }

    private static PriceLog getCurrentPrice(TrackerConfiguration config) throws IOException {

        // For testing, we will use a local html file.
        Document page = null;
        if (config.getIsTest()) {
            System.out.println("In test environment, will parse local html file");
            File localProductFile = new File(PRODUCT_PAGE_PATH);
            page = Jsoup.parse(localProductFile, "UTF-8");

        }
        else {
            
            // For testing, leverage the html pages stored locally.
            // File captchaPage = new File(CAPTCHA_PAGE_PATH);
            // page = Jsoup.parse(captchaPage);

            CaptchaResponse response = CaptchaService.solveCaptcha(AMAZON_IPADTEN_URL);

            if (response.getUrl().contains("error")) {
                return new PriceLog(new BigDecimal(Integer.MAX_VALUE), getFormattedTimestamp());
            }

            if (response.getValidationStatus()) {
                page = Jsoup.connect(response.getUrl())
                    .cookies(convertCookiesToMap(response.getCookies()))
                    .userAgent(USER_AGENT)
                    .get();
            }
        }
        
        Element centerDiv = page.selectFirst("div#centerCol.centerColAlign");

        // We have the center div. Now within that center div, we want the div that shows price
        if (centerDiv == null) {
            System.out.println("Center div was not found on page");
            
        }

        // We want to find the priceDisplay within the center column rather than the whole page because
        // other columns might use the same priceDisplay styling. 
        Element priceDisplayDiv = centerDiv.selectFirst("div[id*=priceDisplay]");
        Elements priceSpans = priceDisplayDiv.select("span.priceToPay");
        StringBuilder sb = new StringBuilder();
        for (Element span : priceSpans) {
            sb.append(span.text());
        }

        // log price into ipad_prices_log.txt
        BigDecimal currentPrice = new BigDecimal(sb.toString().replace("$", ""));
        String formattedTimestamp = getFormattedTimestamp();
        String context = getFormattedTimestamp() + " - " + currentPrice + "\n";
        log(PRICE_LOG_PATH, context);

        return new PriceLog(currentPrice, formattedTimestamp);
    }


    private static void log(String filePath, String log) throws IOException{
        File logFile = new File(filePath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
        writer.write(log);
        writer.close();
    }

    private static String getFormattedTimestamp() {
        Date timestamp = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return dateFormatter.format(timestamp);
    }

    private static Map<String, String> convertCookiesToMap(Set<Cookie> cookies) {
        Map<String, String> cookieMap = new HashMap<>();
        for (Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }
        return cookieMap;
    }

    private static void sendNotification(PriceLog priceLog) throws IOException {
        String body = "Current price of IPad 10th Generation is: " + priceLog.getPrice() + ". Alerted at " + priceLog.getDateFound();
        String subject = "IPad 10th Generation Price Drop!";
        NotificationService.notify(subject, body);

        log(NOTIFICATION_LOG_PATH, body);
    }
}