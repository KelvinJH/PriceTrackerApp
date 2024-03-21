package com.ecfx.model;

import java.util.Set;

import org.openqa.selenium.Cookie;

public class CaptchaResponse {

    public CaptchaResponse(Set<Cookie> cookies, String redirectUrl, boolean isValidated) {
        this.cookies = cookies;
        this.redirectUrl = redirectUrl;
        this.isValidated = isValidated;
    }

    public CaptchaResponse(String html, boolean isValidated) {
        this.html = html;
        this.isValidated = isValidated;
    }

    private Set<Cookie> cookies;
    private String redirectUrl;
    private boolean isValidated;
    private String html;
    
    public Set<Cookie> getCookies() {
        return cookies;
    }

    public String getUrl() {
        return redirectUrl;
    }

    public boolean getValidationStatus() {
        return isValidated;
    }

    public String getHTML() {
        return html;
    }
}
