package org.infinispan.demo.weather.service.rest;

import java.util.Map;

public class StationSummaryResponse {

    private final Map<String, Float[]> meanTemp;
    private final String info = "";

    public StationSummaryResponse(Map<String, Float[]> yearSummaries) {
        this.meanTemp = yearSummaries;
    }
}
