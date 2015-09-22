package org.infinispan.demo.weather.dataloader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import noaaParser.DaySummary;
import noaaParser.Downloader;
import noaaParser.Extractor;
import noaaParser.OpLoader;
import scala.collection.parallel.mutable.ParArray;

import javax.inject.Singleton;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static scala.compat.java8.JFunction.func;

/**
 * Utility to obtain weather data in a consumable state
 */
@Singleton
@SuppressWarnings("unused")
public class DataAcquirer {

    @Inject
    private Downloader downloader;

    @Inject
    private Extractor extractor;

    @Inject
    private OpLoader opLoader;

    @Inject
    @Named("countryCodes")
    String countryCodes;

    @Inject
    @Named("skipDownload")
    Boolean skipDownload;

    @Inject
    @Named("sampleYear")
    String sampleYear;

    public ParArray<DaySummary> prepareData() {
        if (!skipDownload) {
            downloader.download();
            extractor.extractAll();
        }
       List<String> countries = stream(countryCodes.split(",")).collect(toList());
        return opLoader.getSummaries(func(c -> c.day().y() == Integer.valueOf(sampleYear) || c.station().exists(func(d -> d.country().exists(func(s -> countries.contains(s.code())))))));
    }

}
