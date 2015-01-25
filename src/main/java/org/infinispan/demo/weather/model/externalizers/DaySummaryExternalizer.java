package org.infinispan.demo.weather.model.externalizers;


import org.infinispan.demo.weather.model.DaySummary;
import org.infinispan.demo.weather.model.Station;
import org.infinispan.commons.marshall.Externalizer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@SuppressWarnings("unused")
public class DaySummaryExternalizer implements Externalizer<DaySummary> {
    @Override
    public void writeObject(ObjectOutput output, DaySummary object) throws IOException {
        output.writeObject(object.getStation());
        output.writeUTF(object.getYearMonth());
        output.writeUTF(object.getMonth());
        output.writeObject(object.getMinTemp());
        output.writeObject(object.getMaxTemp());
        output.writeObject(object.getAvgTemp());
    }

    @Override
    public DaySummary readObject(ObjectInput input) throws IOException, ClassNotFoundException {
        Station station = (Station) input.readObject();
        String yearMonth = input.readUTF();
        String month = input.readUTF();
        Float minTemp = (Float) input.readObject();
        Float maxTemp = (Float) input.readObject();
        Float avgTemp = (Float) input.readObject();
        return new DaySummary(station, month, yearMonth, avgTemp, minTemp, maxTemp);
    }
}
