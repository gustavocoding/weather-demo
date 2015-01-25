package org.infinispan.demo.weather.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.*;
import org.infinispan.demo.weather.model.DaySummary;
import org.infinispan.lucene.cacheloader.configuration.LuceneLoaderConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.leveldb.configuration.LevelDBStoreConfigurationBuilder;
import org.infinispan.query.Search;
import org.infinispan.transaction.TransactionMode;

import java.util.List;

/**
 * Provides a cache
 */
public class CacheProvider implements Provider<Cache> {

    private final DefaultCacheManager defaultCacheManager;

    @Inject
    public CacheProvider(@Named("dataDir") String dataDir, @Named("cacheDataDir") String cacheDataDir) {
        String userHome = System.getProperty("user.home");
        PersistenceConfigurationBuilder persistence = new ConfigurationBuilder()
                .jmxStatistics().enable()
                .clustering().cacheMode(CacheMode.LOCAL)
                .transaction().transactionMode(TransactionMode.NON_TRANSACTIONAL)
                .persistence();

        List<StoreConfigurationBuilder<?, ?>> stores = persistence.stores();
        String levelDBLocation = userHome + "/" + cacheDataDir + "/store/";
        String luceneLocation = userHome + "/" + cacheDataDir + "/lucene/";

        LevelDBStoreConfigurationBuilder levelDBStore = new LevelDBStoreConfigurationBuilder(persistence).location(levelDBLocation);
        LuceneLoaderConfigurationBuilder luceneStore = new LuceneLoaderConfigurationBuilder(persistence).location(luceneLocation);

        stores.add(levelDBStore);
        stores.add(luceneStore);

        Configuration configuration = persistence.indexing().index(Index.LOCAL)
                .addProperty("default.worker.execution", "async")
                .addProperty("default.indexBase", luceneLocation)
                .build();

        defaultCacheManager = new DefaultCacheManager(configuration);
    }

    @Override
    public Cache get() {
        Cache cache = defaultCacheManager.getCache("summaries");
        Search.getSearchManager(cache).getSearchFactory().addClasses(DaySummary.class);
        return cache;
    }
}
