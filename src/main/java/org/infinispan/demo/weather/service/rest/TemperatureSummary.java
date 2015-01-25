package org.infinispan.demo.weather.service.rest;

public class TemperatureSummary {

    private final String month;
    private final Float temperature;

    public TemperatureSummary(String month, Float temperature) {
        this.month = month;
        this.temperature = temperature;
    }

    public String getMonth() {
        return month;
    }

    public Float getTemperature() {
        return temperature;
    }
}
