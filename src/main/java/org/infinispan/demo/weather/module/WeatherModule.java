package org.infinispan.demo.weather.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import noaaParser.Downloader;
import noaaParser.Extractor;
import noaaParser.OpLoader;
import org.infinispan.Cache;
import scala.compat.java8.JFunction1;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static scala.compat.java8.JFunction.func;


public class WeatherModule extends AbstractModule {

    private static final String HOME_DIR = System.getProperty("user.home");


    @Override
    protected void configure() {
        bind(Cache.class).toProvider(CacheProvider.class).asEagerSingleton();
        Properties properties = new Properties();
        try {
            properties.load(WeatherModule.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Names.bindProperties(binder(), properties);

    }

    @Provides
    @Singleton
    Downloader providesDownloader(@Named("dataDir") String dataDir,
                                  @Named("startYear") int startYear,
                                  @Named("endYear") int endYear) {
        return new Downloader(startYear, endYear, HOME_DIR + "/" + dataDir);
    }

    @Provides
    @Singleton
    Extractor providesExtractor(@Named("dataDir") String dataDir) {
        return new Extractor(HOME_DIR + "/" + dataDir);
    }

    @Provides
    @Singleton
    OpLoader providesOpLoader(@Named("dataDir") String dataDir,
                              @Named("startYear") int startYear,
                              @Named("endYear") int endYear) {

        JFunction1<File, Object> fileFilter = (File f) -> {
            String name = f.getName();
            if (!name.endsWith("op")) return false;
            Integer year = Integer.valueOf(name.substring(name.length() - 7, name.length() - 3));
            return year >= startYear && year <= endYear;
        };
        return new OpLoader(HOME_DIR + "/" + dataDir, func(fileFilter));
    }

}
