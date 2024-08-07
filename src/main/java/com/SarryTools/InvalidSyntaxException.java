package com.SarryTools;

public class InvalidSyntaxException extends RuntimeException{

    public InvalidSyntaxException() {}
    public InvalidSyntaxException(String msg) {
        super(msg);
    }

}
