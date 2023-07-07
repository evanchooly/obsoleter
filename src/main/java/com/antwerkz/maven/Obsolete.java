package com.antwerkz.maven;

public @interface Obsolete {
    /**
     * The target version that will ultimately hide the annotated methods
     *
     * @return the target version
     */
    String value();
}
