package com.oneau.web.util;

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

class NoOpConverter implements Converter {
    public Double convert(Double value) {
        return value;
    }
}

class KmToAuConverter implements Converter {
    public Double convert(Double valueInKilometers) {
        return valueInKilometers / Constants.AU;
    }
}