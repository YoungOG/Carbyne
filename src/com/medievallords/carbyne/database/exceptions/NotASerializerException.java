package com.medievallords.carbyne.database.exceptions;

public class NotASerializerException extends Exception {

    public NotASerializerException() {
        super( "Config given an object which doesn't extend serializer" );
    }
}
