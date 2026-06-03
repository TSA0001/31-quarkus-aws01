package com.example;

public class JmaRainfallResponse {
    private int hourlyRainfall;
    private int total24h;
    private int forecast1h;

    public JmaRainfallResponse() {
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
}
