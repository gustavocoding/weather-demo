package org.infinispan.demo.weather.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.infinispan.commons.marshall.SerializeWith;
import org.infinispan.demo.weather.model.externalizers.StationExternalizer;

@Indexed
@SerializeWith(StationExternalizer.class)
@SuppressWarnings("unused")
public class Station {

    @Field
    private String usaf;

    @Field
    private Integer wban;

    @Field
    private String name;

    @IndexedEmbedded
    private Country country;

    @Field
    private Float latitude;

    @Field
    private Float longitude;

    public Station(String usaf, Integer wban, String name, Country country, Float latitude, Float longitude) {
        this.usaf = usaf;
        this.wban = wban;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsaf() {
        return usaf;
    }

    public Integer getWban() {
        return wban;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

}
