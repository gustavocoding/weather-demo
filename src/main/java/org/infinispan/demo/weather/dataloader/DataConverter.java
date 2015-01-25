package org.infinispan.demo.weather.dataloader;

import noaaParser.Country;
import noaaParser.DaySummary;
import noaaParser.Station;
import noaaParser.YMD;
import scala.Option;

import java.util.function.Function;

/**
 * Converter for DaySummaries between the NOAA parser format and
 * the model format
 */
@SuppressWarnings("unused")
public class DataConverter {

    private DataConverter() {
    }

    public static Function<DaySummary, org.infinispan.demo.weather.model.DaySummary> converter = input -> {
        float avgTemp = input.avgTemp().toCelsius();
        float minTemp = input.minTemp().toCelsius();
        float maxTemp = input.maxTemp().toCelsius();
        YMD date = input.day();
        Option<Station> station = input.station();
        org.infinispan.demo.weather.model.Station _station = null;
        if (station.isDefined()) {
            Station sourceStation = station.get();
            String name = sourceStation.name();
            String usaf = sourceStation.usaf();
            int wban = sourceStation.wban();
            Option<Object> latitude = sourceStation.latitude();
            Option<Object> longitude = sourceStation.longitude();
            Float _lat = latitude.isDefined() ? (Float) latitude.get() : null;
            Float _long = longitude.isDefined() ? (Float) longitude.get() : null;
            Option<Country> country = sourceStation.country();
            org.infinispan.demo.weather.model.Country _country = null;
            if (country.isDefined()) {
                Country sourceCountry = country.get();
                String countryName = sourceCountry.name();
                String code = sourceCountry.code();
                _country = new org.infinispan.demo.weather.model.Country(countryName, code);
            }
            _station = new org.infinispan.demo.weather.model.Station(usaf, wban, name, _country, _lat, _long);
        }
        return new org.infinispan.demo.weather.model.DaySummary(
                _station, String.valueOf(date.m()), String.valueOf(date.y()) + String.valueOf(date.m()), avgTemp, minTemp, maxTemp);

    };
}
