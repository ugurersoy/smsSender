package com.acme.customization.ws.maradit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* User: haver
* Date: 23/10/12
* Time: 16:56
*/
public class Settings {
    public Balance balance = new Balance();
    public List<Keyword> keywords = new ArrayList<Keyword>();
    public List<Sender> senders = new ArrayList<Sender>();

    public static class Balance{
        public Double limit;
        public Double main;
    }

    public static class Keyword{
        public String serviceNumber;
        public Date timestamp;
        public short validity;
        public String value;
    }

    public static class Sender{
        public Boolean isDefault;
        public String value;
    }
}
