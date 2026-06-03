package com.example;

import java.time.LocalDateTime;

public class RainfallData {
    private String location;
    private int hourlyRainfall;
    private int total24h;
    private int forecast1h;
    private LocalDateTime timestamp;

    public RainfallData() {
    }

    public RainfallData(String location, int hourlyRainfall, int total24h, int forecast1h, LocalDateTime timestamp) {
        this.location = location;
        this.hourlyRainfall = hourlyRainfall;
        this.total24h = total24h;
        this.forecast1h = forecast1h;
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getHourlyRainfall() {
        return hourlyRainfall;
    }

    public void setHourlyRainfall(int hourlyRainfall) {
        this.hourlyRainfall = hourlyRainfall;
    }

    public int getTotal24h() {
        return total24h;
    }

    public void setTotal24h(int total24h) {
        this.total24h = total24h;
    }

    public int getForecast1h() {
        return forecast1h;
    }

    public void setForecast1h(int forecast1h) {
        this.forecast1h = forecast1h;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
