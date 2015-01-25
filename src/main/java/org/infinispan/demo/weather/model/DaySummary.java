package org.infinispan.demo.weather.model;

import org.infinispan.demo.weather.model.externalizers.DaySummaryExternalizer;
import org.hibernate.search.annotations.*;
import org.infinispan.commons.marshall.SerializeWith;

@Indexed
@SerializeWith(DaySummaryExternalizer.class)
@SuppressWarnings("unused")
public class DaySummary  {

    @IndexedEmbedded
    private Station station;

    @Field(store = Store.YES, analyze = Analyze.NO)
    private String yearMonth;

    @Field(store = Store.YES, analyze = Analyze.NO)
    private String month;

    @Field(store = Store.YES)
    Float avgTemp;

    @Field(store = Store.YES)
    Float minTemp;

    @Field(store = Store.YES)
    Float maxTemp;

    public String getYearMonth() {
        return yearMonth;
    }

    public String getMonth() {
        return month;
    }

    public DaySummary(Station station, String month, String yearMonth, Float avgTemp, Float minTemp, Float maxTemp) {
        this.station = station;
        this.yearMonth = yearMonth;
        this.month = month;
        this.avgTemp = avgTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public Station getStation() {
        return station;
    }

    public Float getAvgTemp() {
        return avgTemp;
    }

    public Float getMinTemp() {
        return minTemp;
    }

    public Float getMaxTemp() {
        return maxTemp;
    }

}
