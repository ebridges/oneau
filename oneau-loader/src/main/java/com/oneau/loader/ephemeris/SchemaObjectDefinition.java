package com.oneau.loader.ephemeris;

import static com.oneau.loader.ephemeris.ObjectType.SCHEMA;
import static com.oneau.loader.ephemeris.ObjectType.TABLE;

public class SchemaObjectDefinition {
    private ObjectType objectType;
    private String name;

    public static final SchemaObjectDefinition SCHEMANAME = new SchemaObjectDefinition(SCHEMA, "oneau");
    public static final SchemaObjectDefinition CONSTANT = new SchemaObjectDefinition(TABLE, "constant");
    public static final SchemaObjectDefinition EPHEMERIS_DATA = new SchemaObjectDefinition(TABLE, "ephemeris_data");
    public static final SchemaObjectDefinition EPHEMERIS_HEADER = new SchemaObjectDefinition(TABLE, "ephemeris_header");
    public static final SchemaObjectDefinition EPHEMERIS_INTERVAL = new SchemaObjectDefinition(TABLE, "ephemeris_interval");
    public static final SchemaObjectDefinition MEASURED_ITEM = new SchemaObjectDefinition(TABLE, "measured_item");
    public static final SchemaObjectDefinition OBSERVATION = new SchemaObjectDefinition(TABLE, "observation");

    private SchemaObjectDefinition(ObjectType objectType, String name) {
        this.objectType = objectType;
        this.name = name;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}

enum ObjectType {
    SCHEMA,
    TABLE
}

