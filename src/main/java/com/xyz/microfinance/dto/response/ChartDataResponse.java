package com.xyz.microfinance.dto.response;

public class ChartDataResponse {
    private String day;
    private int value;

    public ChartDataResponse() {}

    public ChartDataResponse(String day, int value) {
        this.day = day;
        this.value = value;
    }

    // Getters and Setters
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
}
