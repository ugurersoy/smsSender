package com.java.net.maradit.api;

/**
 * User: haver
 * Date: 23/10/12
 * Time: 16:55
 */
public class Response {
    /*
     * True if we receive HTTP status code 200, else false (possible error caused by client)
     * */
     public Boolean status = false;

    /*
     * If status field is false, holds information about cause
     * */
    public String error;

    /*
     * XML Service response status code. Codes are similar to HTTP status codes. Value 200
     * means that request was successful.
     * */
    public int statusCode = 0;

    /*
     * XML Service response description. In case of error contains meaningful explanation.
     * */
    public String statusDescription;

    /*
     * Holds raw XML which we received
     * */
    public String xml;
}
