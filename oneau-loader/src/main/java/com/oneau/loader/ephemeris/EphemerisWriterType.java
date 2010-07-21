package com.oneau.loader.ephemeris;

/**
 * User: ebridges
 * Date: Jul 19, 2010
 */
public enum EphemerisWriterType {
    CSV_WRITER(EphemerisCsvWriter.class);
    private Class<? extends EphemerisWriter> writerClass;

    EphemerisWriterType(Class<? extends EphemerisWriter> writerClass) {
        this.writerClass = writerClass;
    }

    public Class<? extends EphemerisWriter> getWriterClass() {
        return writerClass;
    }
}
