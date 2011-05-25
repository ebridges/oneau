package com.oneau.data;

public class SchemaObjectDefinition {
    private ObjectType objectType;
    private String name;

    public static final SchemaObjectDefinition SCHEMANAME = new SchemaObjectDefinition(ObjectType.SCHEMA, "oneau");
    public static final SchemaObjectDefinition CONSTANT = new SchemaObjectDefinition(ObjectType.TABLE, "constant");
    public static final SchemaObjectDefinition EPHEMERIS_DATA = new SchemaObjectDefinition(ObjectType.TABLE, "ephemeris_data");
    public static final SchemaObjectDefinition EPHEMERIS_HEADER = new SchemaObjectDefinition(ObjectType.TABLE, "ephemeris_header");
    public static final SchemaObjectDefinition EPHEMERIS_INTERVAL = new SchemaObjectDefinition(ObjectType.TABLE, "ephemeris_interval");
    public static final SchemaObjectDefinition MEASURED_ITEM = new SchemaObjectDefinition(ObjectType.TABLE, "measured_item");
    public static final SchemaObjectDefinition OBSERVATION = new SchemaObjectDefinition(ObjectType.TABLE, "observation");

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