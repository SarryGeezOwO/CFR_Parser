package com.SarryTools;

public class PropertyNotFoundException extends RuntimeException{

    public PropertyNotFoundException() {}
    public PropertyNotFoundException(String msg) {
        super(msg);
    }
}
