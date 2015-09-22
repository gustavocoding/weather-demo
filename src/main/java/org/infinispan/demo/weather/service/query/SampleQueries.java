package org.infinispan.demo.weather.service.query;


import com.google.inject.Inject;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.infinispan.Cache;
import org.infinispan.demo.weather.model.DaySummary;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;

import java.util.List;

public class SampleQueries {

    @Inject
    private Cache cache;

    public int countDocs() {
        MatchAllDocsQuery allDocsQuery = new MatchAllDocsQuery();
        CacheQuery query = Search.getSearchManager(cache).getQuery(allDocsQuery, DaySummary.class);
        return query.getResultSize();
    }

    public List<Object> queryParser() throws ParseException {

        QueryParser queryParser = new QueryParser("default", new StandardAnalyzer());
        Query luceneQuery = queryParser.parse("+station.name:airport +yearMonth:201412 +(avgTemp < 0)");

        CacheQuery query = Search.getSearchManager(cache).getQuery(luceneQuery, DaySummary.class);
        return query.list();
    }

    public static void main(String[] args) throws ParseException {
        new SampleQueries().queryParser();
    }

}
