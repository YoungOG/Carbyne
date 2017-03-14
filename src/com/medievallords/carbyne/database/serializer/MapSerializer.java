package com.medievallords.carbyne.database.serializer;

import com.medievallords.carbyne.database.AbstractSerializer;
import com.mongodb.util.JSON;

import java.util.HashMap;

public class MapSerializer extends AbstractSerializer<HashMap> {

    @Override
    public String toString(HashMap data) {
        return JSON.serialize(data);
    }

    @Override
    public HashMap fromString(Object data) {
        return (HashMap) JSON.parse(((String) data));
    }
}