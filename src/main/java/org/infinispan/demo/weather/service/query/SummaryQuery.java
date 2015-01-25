package org.infinispan.demo.weather.service.query;

import com.google.inject.Inject;
import org.infinispan.demo.weather.model.DaySummary;
import org.infinispan.demo.weather.service.rest.TemperatureSummary;
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
import java.util.ArrayList;
import java.util.List;


public class SummaryQuery {

    @Inject
    private Cache cache;

    public List<TemperatureSummary> getSummary(String year, String usaf) throws IOException {
        // Query by year and usaf
        TermQuery q_usaf = new TermQuery(new Term("station.usaf", usaf));
        TermQuery q_year = new TermQuery(new Term("year", year));
        BooleanQuery q_bool = new BooleanQuery();
        q_bool.add(q_usaf, BooleanClause.Occur.MUST);
        q_bool.add(q_year, BooleanClause.Occur.MUST);

        // Group by month and extract maxTemp from each month
        IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(DaySummary.class);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        GroupingSearch groupingSearch = new GroupingSearch("month");
        groupingSearch.setGroupDocsLimit(365);
        groupingSearch.setSortWithinGroup(new Sort(new SortField[]{new SortField("maxTemp", SortField.Type.FLOAT)}));
        groupingSearch.setGroupSort(new Sort(new SortField[]{new SortField("month", SortField.Type.INT)}));
        groupingSearch.setFillSortFields(true);
        TopGroups<Object> topGroups = groupingSearch.search(searcher, q_bool, 0, 12);

        List<TemperatureSummary> temperatureSummaries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            GroupDocs<Object> group = topGroups.groups[i];
            String groupValue = new String(((BytesRef) group.groupValue).bytes);
            FieldDoc scoreDoc = (FieldDoc) group.scoreDocs[group.scoreDocs.length - 1];
            temperatureSummaries.add(new TemperatureSummary(groupValue, Float.valueOf(scoreDoc.fields[0].toString())));
        }
        return temperatureSummaries;

    }

}
