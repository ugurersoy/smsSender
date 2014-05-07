package com.acme.customization.ws.maradit;

import java.util.Date;

/**
* User: haver
* Date: 23/10/12
* Time: 16:55
*/
public class ReportDetail {
    public long id;
    public String msisdn;
    public String payload;
    public String xser;
    public Double cost;
    public int errorCode;
    public Date lastUpdated;
    public Date submitted;
    public short network;
    public byte sequence;
    public State state;
}
