package com.ecfx.model;

public class TrackerConfiguration {

    private boolean isTest;
    private int timeBetweenPoll;

    public TrackerConfiguration() {}

    public void setTestConfiguration(boolean isTest) {
        this.isTest = isTest;
    }
    
    public void setTimeBetweenPoll(int seconds) {
        this.timeBetweenPoll = seconds;
    }

    public boolean getIsTest() { return isTest; };
    public int getTimeBetweenPoll() { return timeBetweenPoll; };
} 
