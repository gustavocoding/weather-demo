package org.infinispan.demo.weather.model;

import org.infinispan.demo.weather.model.externalizers.CountryExternalizer;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.hibernate.search.annotations.*;
import org.infinispan.commons.marshall.SerializeWith;

@Indexed
@AnalyzerDef(name = "lowercase",
        tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),
        filters = {@TokenFilterDef(factory = LowerCaseFilterFactory.class)}
)
@SerializeWith(CountryExternalizer.class)
@SuppressWarnings("unused")
public class Country {

    @Field(store = Store.YES)
    @Analyzer(definition = "lowercase")
    private String name;

    @Field(store = Store.YES, analyze = Analyze.NO)
    private String code;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
