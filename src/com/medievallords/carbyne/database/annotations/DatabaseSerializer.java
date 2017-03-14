package com.medievallords.carbyne.database.annotations;

import com.medievallords.carbyne.database.AbstractSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface DatabaseSerializer {

    Class<? extends AbstractSerializer> serializer();
}
