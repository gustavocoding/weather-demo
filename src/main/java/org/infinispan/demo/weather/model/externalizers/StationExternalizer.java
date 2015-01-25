package org.infinispan.demo.weather.model.externalizers;

import org.infinispan.commons.marshall.Externalizer;
import org.infinispan.demo.weather.model.Country;
import org.infinispan.demo.weather.model.Station;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@SuppressWarnings("unused")
public class StationExternalizer implements Externalizer<Station> {
    @Override
    public void writeObject(ObjectOutput output, Station object) throws IOException {
        output.writeObject(object.getCountry());
        output.writeObject(object.getLatitude());
        output.writeObject(object.getLongitude());
        output.writeInt(object.getWban());
        output.writeUTF(object.getName());
        output.writeUTF(object.getUsaf());
    }

    @Override
    public Station readObject(ObjectInput input) throws IOException, ClassNotFoundException {
        Country country = (Country) input.readObject();
        Float latitude = (Float) input.readObject();
        Float longitude = (Float) input.readObject();
        int wban = input.readInt();
        String name = input.readUTF();
        String usaf = input.readUTF();
        return new Station(usaf, wban, name, country, latitude, longitude);
    }
}
