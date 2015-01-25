package org.infinispan.demo.weather.service.rest;


public class Suggestion {
    private final String suggestion;
    private final String code;

    public Suggestion(String suggestion, String usaf) {
        this.suggestion = suggestion;
        this.code = usaf;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getCode() {
        return code;
    }

}
