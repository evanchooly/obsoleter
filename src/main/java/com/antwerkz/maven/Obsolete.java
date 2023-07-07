package com.antwerkz.maven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Obsolete {
    /**
     * The version in which the annotated element was obsoleted.
     *
     * @return the obsolete version
     */
    String value();
}
