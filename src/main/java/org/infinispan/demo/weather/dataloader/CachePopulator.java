package org.infinispan.demo.weather.dataloader;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.infinispan.Cache;
import scala.collection.parallel.mutable.ParArray;

import static org.infinispan.demo.weather.dataloader.DataConverter.converter;

@Singleton
@SuppressWarnings("unused")
public class CachePopulator {

    @Inject
    private Cache cache;

    @Inject
    private DataAcquirer dataAcquirer;

    @Inject
    @Named("skipCacheLoad")
    Boolean skipCacheLoad;

    @SuppressWarnings("unchecked")
    public void indexData() {
        if (!skipCacheLoad) {
            ParArray<noaaParser.DaySummary> summaries = dataAcquirer.prepareData();
            int size = summaries.size();
            for (int i = 0; i < size; i++) {
                System.out.printf("\rIndexing %d of %d", i, size);
                noaaParser.DaySummary daySummary = summaries.apply(i);
                cache.put(String.valueOf(i), converter.apply(daySummary));
            }
            System.out.printf("\nIndexing done.");
            System.out.println();
        }
    }

}
