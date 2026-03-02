package com.gamerlink.profile.exception;

public class HandleImmutableException extends RuntimeException {
    public HandleImmutableException(){
        super("Handle is immutable");
    }
}
