package com.medievallords.carbyne.database.serializer;

import com.medievallords.carbyne.database.AbstractSerializer;
import com.mongodb.util.JSON;

import java.util.HashMap;

/**
 * Created by Chris on 2/8/2017.
 */
public class MoneySerializer extends AbstractSerializer<HashMap> {


    @Override
    public String toString(HashMap data) {
        return JSON.serialize(data);
    }

    @Override
    public HashMap fromString(Object data) {
        if(data instanceof String){
            String s = (String) data;
            HashMap<String,Integer> moneyMap = (HashMap<String,Integer>) JSON.parse(s);
            return moneyMap;
        }
        return null;
    }
}
