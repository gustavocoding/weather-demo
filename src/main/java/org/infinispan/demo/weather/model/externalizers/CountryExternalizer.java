package org.infinispan.demo.weather.model.externalizers;

import org.infinispan.commons.marshall.Externalizer;
import org.infinispan.demo.weather.model.Country;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@SuppressWarnings("unused")
public class CountryExternalizer implements Externalizer<Country> {

    @Override
    public void writeObject(ObjectOutput output, Country object) throws IOException {
        output.writeUTF(object.getCode());
        output.writeUTF(object.getName());
    }

    @Override
    public Country readObject(ObjectInput input) throws IOException, ClassNotFoundException {
        String code = input.readUTF();
        String name = input.readUTF();
        return new Country(code, name);
    }
}
