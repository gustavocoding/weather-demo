package org.infinispan.demo.weather.service.query;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.infinispan.Cache;
import org.infinispan.demo.weather.model.DaySummary;
import org.infinispan.demo.weather.service.rest.Suggestion;
import org.infinispan.query.Search;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provide suggestion over the countries present in the dataset
 */
@Singleton
public class LuceneCountrySuggester {

    @Inject
    private Cache cache;

    private volatile boolean initialized = false;
    private AnalyzingSuggester suggester;

    public void buildDictionary() throws IOException {
        System.out.printf("Building dictionary...");
        IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(DaySummary.class);
        suggester = new AnalyzingSuggester(new CountryAnalyzer());
        DocumentDictionary dictionary = new DocumentDictionary(indexReader, "station.country.name", null, "station.country.code");
        suggester.build(dictionary);
        initialized = true;
        System.out.printf("Dictionary done.\n");
    }

    public List<Suggestion> suggest(String prefix, int numberSuggestions) throws IOException {
        if (!initialized) throw new RuntimeException("Dictionary not built");
        List<Lookup.LookupResult> firstSuggestion = suggester.lookup(prefix, false, numberSuggestions);
        return firstSuggestion
                .stream()
                .map(f -> new Suggestion(f.key.toString(), new String(f.payload.bytes)))
                .collect(Collectors.<Suggestion>toList());
    }

    private static class CountryAnalyzer extends Analyzer {
        @Override
        protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
            KeywordTokenizer keywordTokenizer = new KeywordTokenizer(reader);
            LowerCaseFilter lowerCaseFilter = new LowerCaseFilter(keywordTokenizer);
            return new TokenStreamComponents(keywordTokenizer, lowerCaseFilter);
        }
    }

}
