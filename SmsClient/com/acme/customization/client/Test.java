package com.acme.customization.client;

import java.util.ArrayList;
import java.util.List;

import com.acme.customization.ws.maradit.Envelope;
import com.acme.customization.ws.maradit.Maradit;
import com.acme.customization.ws.maradit.ReportDetail;
import com.acme.customization.ws.maradit.ReportResponse;
import com.acme.customization.ws.maradit.Response;
import com.acme.customization.ws.maradit.Settings;
import com.acme.customization.ws.maradit.SettingsResponse;
import com.acme.customization.ws.maradit.SubmitResponse;

public class Test {

    public static void main(String[] args) {

        Maradit maradit = new Maradit("user name", "good secret");
        maradit.validityPeriod = 120;
        //maradit.scheduledDeliveryTime = new Date();
        //maradit.from = "BILIMSA";
        //maradit.dataCoding = DataCoding.UCS2;

        /*
           * One message text to many cell phone numbers
           * */
        List<String> to = new ArrayList<String>();
        to.add("905001112230");
        //to.add("905001112233");
        SubmitResponse response = maradit.submit(to, "hello world");
        printResponse(response);
        System.out.println("Message Id (if status = true and statusCode = 200):" + response.messageId);

        /*
           * Personalized messages
           * */
        List<Envelope> envelopes = new ArrayList<Envelope>();
        envelopes.add(new Envelope("905001112230", "hello world 1"));
        //envelopes.add(new Envelope("905001112233", "hello world 2"));
        SubmitResponse submitMultiResponse = maradit.submitMulti(envelopes);
        printResponse(submitMultiResponse);
        System.out.println("Message Id (if status = true and statusCode = 200):" + submitMultiResponse.messageId);

        /*
         * Check query response
         * */
        ReportResponse reportResponse = maradit.query(9735448, "");
        //ReportResponse reportResponse = maradit.query(9735448, "905001112233"); // filter by msisdn
        printResponse(reportResponse);
        if (reportResponse.status && reportResponse.statusCode == 200) {

            for (ReportDetail item : reportResponse.reportDetails) {
                System.out.println(
                        "Id:" + item.id + "\t" +
                                "Submitted:" + item.submitted + "\t" +
                                "Last Updated:" + item.lastUpdated + "\t" +
                                "MSISDN:" + item.msisdn + "\t" +
                                "State:" + item.state + "\t" +
                                "Error Code:" + item.errorCode + "\t" +
                                "Payload:" + item.payload + "\t"
                );
            }
        }

        /*
         * Cancel SMS sending
         * */
        Response cancelResponse = maradit.cancel(9748949);
        printResponse(cancelResponse);
        if (cancelResponse.status && cancelResponse.statusCode == 200) {
            System.out.println("Message is canceled");
        }

        /*
         * Get account settings
         * */
        SettingsResponse settingsResponse = maradit.getSettings();
        printResponse(settingsResponse);
        if (settingsResponse.status && settingsResponse.statusCode == 200) {

            System.out.println("User info:");
            System.out.println("Balance limit:" + settingsResponse.settings.balance.limit + "\t" +
                    "Main balance:" + settingsResponse.settings.balance.main);

            System.out.println("Senders:");
            for (Settings.Sender sender : settingsResponse.settings.senders) {
                System.out.println("Sender:" + sender.value + "\t Is Default:" + sender.isDefault);
            }

            System.out.println("Keywords:");
            for (Settings.Keyword keyword : settingsResponse.settings.keywords) {
                System.out.println("Keyword:" + keyword.value + "\t Service Number:" + keyword.serviceNumber +
                        "\t Timestamp:" + keyword.timestamp + "\t Validity:" + keyword.validity);
            }
        }
    }

    public static void printResponse(Response response) {
        System.out.println("Data post status:" + response.status);
        System.out.println("Data post error (if status false):" + response.error);
        System.out.println("Gateway status code:" + response.statusCode);
        System.out.println("Gateway status description:" + response.statusDescription);
        System.out.println("Raw response xml:" + response.xml);
    }
}
