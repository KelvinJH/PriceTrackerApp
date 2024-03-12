package com.ecfx.model;

import java.util.Set;

import org.openqa.selenium.Cookie;

public class CaptchaResponse {

    public CaptchaResponse(Set<Cookie> cookies, String redirectUrl, boolean isValidated) {
        this.cookies = cookies;
        this.redirectUrl = redirectUrl;
        this.isValidated = isValidated;
    }

    public CaptchaResponse(boolean isValidated) {
        this.isValidated = isValidated;
    }

    private Set<Cookie> cookies;
    private String redirectUrl;
    private boolean isValidated;
    
    public Set<Cookie> getCookies() {
        return cookies;
    }

    public String getUrl() {
        return redirectUrl;
    }

    public boolean getValidationStatus() {
        return isValidated;
    }
}
