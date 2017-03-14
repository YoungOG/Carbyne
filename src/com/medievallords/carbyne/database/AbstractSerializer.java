package com.medievallords.carbyne.database;

public abstract class AbstractSerializer<TYPE> {

    public AbstractSerializer() {}

    public abstract String toString(TYPE data);

    public abstract TYPE fromString(Object data);
}