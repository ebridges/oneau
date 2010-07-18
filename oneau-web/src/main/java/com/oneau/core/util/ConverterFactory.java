package com.oneau.core.util;

/**
 * User: EBridges
 * Created: 2010-04-19
 */
public class ConverterFactory {
    public static Converter getConverter(Converter.TYPE type) {
        if (type == Converter.TYPE.AU) {
            return new KmToAuConverter();
        } else if (type == Converter.TYPE.KM) {
            return new NoOpConverter();
        } else {
            throw new IllegalArgumentException("unrecognized converter type.");
        }
    }
}

class KmToAuConverter implements Converter {
    @Override
    public Double convert(Double valueInKilometers) {
        return valueInKilometers / Constants.AU;
    }
}

class NoOpConverter implements Converter {
    @Override
    public Double convert(Double value) {
        return value;
    }
}