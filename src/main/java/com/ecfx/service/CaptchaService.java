package com.ecfx.service;


import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.ecfx.model.CaptchaResponse;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CaptchaService {
    
    public static CaptchaResponse solveCaptcha(String url) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(url);
            WebElement captchaInput = driver.findElement(By.id("captchacharacters"));

            String userInput = JOptionPane.showInputDialog(null, "Enter your input:");

            captchaInput.sendKeys(userInput);

            WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
            submitButton.click();

            return new CaptchaResponse(driver.manage().getCookies(), driver.getCurrentUrl(), true);
        } catch (NoSuchElementException ex) {
            // Captcha field was not found - We don't need to authenticate
            return new CaptchaResponse(true);
        } finally {
            driver.quit();
        }
    }
}
