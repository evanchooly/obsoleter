package com.antwerkz.maven;

public class ObsoletedMethods {

    @Obsolete("now")
    public String dummy() {
        return "yo!";
    }

    public String forever() {
        return "forever";
    }
}
