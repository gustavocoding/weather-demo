package org.infinispan.demo.weather.service.query;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.infinispan.demo.weather.model.DaySummary;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.util.BytesRef;
import org.infinispan.Cache;
import org.infinispan.query.Search;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class StationSummaryQuery {

    @Inject
    private Cache cache;

    public Map<String, Float[]> getStationSummary(String usaf) throws IOException {
        Map<String, Float[]> results = new HashMap<>();
        IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(DaySummary.class);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        // Query by year and usaf
        TermQuery queryByUsaf = new TermQuery(new Term("station.usaf", usaf));

        // Group by month and extract maxTemp from each month
        GroupingSearch groupingSearch = new GroupingSearch("yearMonth");
        groupingSearch.setGroupDocsLimit(2000);
        groupingSearch.setSortWithinGroup(new Sort(new SortField("maxTemp", SortField.Type.FLOAT)));
        groupingSearch.setGroupSort(new Sort(new SortField[]{new SortField("yearMonth", SortField.Type.INT)}));
        groupingSearch.setFillSortFields(true);
        TopGroups<Object> topGroups = groupingSearch.search(searcher, queryByUsaf, 0, 2000);

        int length = topGroups.groups.length;

        for (int i = 0; i < length; i++) {
            GroupDocs<Object> group = topGroups.groups[i];
            String groupValue = new String(((BytesRef) group.groupValue).bytes);
            FieldDoc scoreDoc = (FieldDoc) group.scoreDocs[group.scoreDocs.length - 1];
            Float temp = Float.valueOf(scoreDoc.fields[0].toString());
            String year = year(groupValue);
            int month = month(groupValue);
            Float[] values;
            if (!results.containsKey(year)) {
                values = new Float[12];
            } else {
                values = results.get(year);
            }
            values[month - 1] = temp;
            results.put(year, values);
        }
        return results;
    }

    private int month(String groupValue) {
        return Integer.valueOf(groupValue.substring(4, groupValue.length()));
    }

    private String year(String yearMonth) {
        return yearMonth.substring(0, 4);
    }

    public Map<String, Float[]> getCountrySummary(String countryCode) throws IOException {
        Map<String, Float[]> results = new HashMap<>();
        IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(DaySummary.class);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        // Query by country
        TermQuery queryByCountry = new TermQuery(new Term("station.country.code", countryCode));

        // Group by month and extract maxTemp from each month
        GroupingSearch groupingSearch = new GroupingSearch("yearMonth");
        groupingSearch.setGroupDocsLimit(2000);
        groupingSearch.setSortWithinGroup(new Sort(new SortField("maxTemp", SortField.Type.FLOAT)));
        groupingSearch.setGroupSort(new Sort(new SortField[]{new SortField("yearMonth", SortField.Type.STRING)}));
        groupingSearch.setFillSortFields(true);
        TopGroups<Object> topGroups = groupingSearch.search(searcher, queryByCountry, 0, 2000);

        int length = topGroups.groups.length;

        for (int i = 0; i < length; i++) {
            GroupDocs<Object> group = topGroups.groups[i];
            String groupValue = new String(((BytesRef) group.groupValue).bytes);
            Float maxTemp = findMaxTemp(group.scoreDocs, group.scoreDocs.length - 1);
            String year = year(groupValue);
            int month = month(groupValue);
            Float[] values;
            if (!results.containsKey(year)) {
                values = new Float[12];
            } else {
                values = results.get(year);
            }
            values[month - 1] = maxTemp;
            results.put(year, values);
        }
        return results;
    }

    private float findMaxTemp(ScoreDoc[] scoreDocs, int idx) {
        FieldDoc scoreDoc = (FieldDoc) scoreDocs[idx];
        Float temp = Float.valueOf(scoreDoc.fields[0].toString());
        return temp > 100 ? findMaxTemp(scoreDocs, idx - 1) : temp;
    }
}


