package com.java.net.maradit.api;

/**
* User: haver
* Date: 23/10/12
* Time: 16:56
*/
public class Envelope {
    public String message;
    public String to;

    public Envelope(String to, String message) {
        this.to = to;
        this.message = message;
    }
}
