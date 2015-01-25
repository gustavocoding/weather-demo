package org.infinispan.demo.weather.service.rest;

import com.google.inject.Inject;
import org.infinispan.demo.weather.service.query.LuceneCountrySuggester;
import org.infinispan.demo.weather.service.query.StationSummaryQuery;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/weather")
@Singleton
public class WeatherResource {

    @Inject
    private LuceneCountrySuggester luceneCountrySuggester;

    @Inject
    private StationSummaryQuery summaryQuery;

    @GET
    @Path("/suggest")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Suggestion> suggest(@QueryParam("q") String prefix) throws IOException {
        return luceneCountrySuggester.suggest(prefix, 50000);
    }

    @GET
    @Path("/countrySummary")
    @Produces(MediaType.APPLICATION_JSON)
    public StationSummaryResponse getCountrySummary(@QueryParam("country") String countryCode) throws IOException {
        return new StationSummaryResponse(summaryQuery.getCountrySummary(countryCode));
    }

}
