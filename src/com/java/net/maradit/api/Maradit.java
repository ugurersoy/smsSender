package com.java.net.maradit.api;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * @author 		Muamer Bajric
 * @version 	1.0
 * @date 		2011-11-23
 */
public class Maradit {

    /*
      * Constants
      * */
    final String url = "gw.maradit.net";
    final Boolean useHttps = false;
    final Boolean debug = false;

    /*
      * Username of the account
      * */
    String username = "username";

    /*
      * The current password of the account
      * */
    String password = "password";

    /*
      * The source/sender address that the message will appear to come from.
      * Valid international format number between 1 and 16 characters long,
      * or 11 character alphanumeric string. If account is not allowed to use
      * dynamic sender, it must be predefined at SMS gateway. If this field is
      * left empty default sender will be used.
      * */
    public String from = "";

    /*
      * This parameter specifies the scheduled date time at which the message
      * delivery should be first attempted. When empty message will be delivered
      * immediately. Date time format should be ISO 8601 (2011-05-01T00:00:00 or
      * 2011-05-01T00:00:00+01:00). If time zone offset is not specified, system
      * will consider that the time is in UTC time zone.
      * */
    public Date scheduledDeliveryTime;

    /*
      * The validity period in minutes relative to the time in scheduledDeliveryTime
      * field. Minimum 60 minutes, maximum 1710 minutes.
      * */
    public int validityPeriod = 1440;

    /*
      * Enumeration : Default or UCS2
      * Default: GSM 7 bit default alphabet, http://en.wikipedia.org/wiki/GSM_03.38
      * UCS2   : Unicode encoding, http://en.wikipedia.org/wiki/UCS-2
      * */
    public DataCoding dataCoding = DataCoding.Default;

    public Maradit(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /*
      * Send one SMS (text) to many cell phone numbers. (1toN)
      * */
    public SubmitResponse submit(List<String> to, String message){
        SubmitResponse submitResponse = new SubmitResponse();

        if(to.isEmpty()){
            submitResponse.error = "Enter at least one cell phone number.";
            return submitResponse;
        }

        if(message == ""){
            submitResponse.error = "Message text is empty.";
            return submitResponse;
        }

        Writer output = new StringWriter();
        XMLStreamWriter writer = startDocument("Submit", output);

        if(writer == null){
            submitResponse.error = "Can not create XMLStreamWriter.";
            return submitResponse;
        }

        try {
            writeCredential(writer);
            writeDataCoding(writer);
            writeHeader(writer);
            writeMessage(writer, to, message);
            endDocument(writer);
        } catch (Exception e) {
            submitResponse.error = e.getMessage();
            return submitResponse;
        }

        if(this.debug){
            System.out.println(output.toString());
        }

        Response response = httpPost("Submit", output.toString());

        // Check if HTTP request is OK
        if(response.status){
            submitResponse.error = response.error;
            submitResponse.status = response.status;
            submitResponse.xml = response.xml;

            try {
                readSubmitResponse(response.xml, submitResponse);
            }
            catch (Exception e) {
                submitResponse.error = e.getMessage();
            }
        }

        return submitResponse;
    }

    /*
      * Send personalized SMS messages. (NtoN)
      * */
    public SubmitResponse submitMulti(List<Envelope> envelopes){
        SubmitResponse submitResponse = new SubmitResponse();

        if(envelopes.isEmpty()){
            submitResponse.error = "Enter at least one record of cell phone number and message text.";
            return submitResponse;
        }

        Writer output = new StringWriter();
        XMLStreamWriter writer = startDocument("SubmitMulti", output);

        if(writer == null){
            submitResponse.error = "Can not create XMLStreamWriter.";
            return submitResponse;
        }

        try {
            writeCredential(writer);
            writeDataCoding(writer);
            writeEnvelope(writer, envelopes);
            writeHeader(writer);
            endDocument(writer);
        } catch (Exception e) {
            submitResponse.error = e.getMessage();
            return submitResponse;
        }

        if(this.debug){
            System.out.println(output.toString());
        }

        Response response = httpPost("SubmitMulti", output.toString());

        // Check if HTTP request is OK
        if(response.status){
            submitResponse.error = response.error;
            submitResponse.status = response.status;
            submitResponse.xml = response.xml;

            try {
                readSubmitResponse(response.xml, submitResponse);
            }
            catch (Exception e) {
                submitResponse.error = e.getMessage();
            }
        }

        return submitResponse;
    }

    /*
      * Query message status
      * */
    public ReportResponse query(long messageId, String msisdn){
        ReportResponse reportResponse = new ReportResponse();

        Writer output = new StringWriter();
        XMLStreamWriter writer = startDocument("Query", output);

        if(writer == null){
            reportResponse.error = "Can not create XMLStreamWriter.";
            return reportResponse;
        }

        try {
            writeCredential(writer);
            writeMSISDN(writer, msisdn);
            writeMessageId(writer, messageId);
            endDocument(writer);
        } catch (Exception e) {
            reportResponse.error = e.getMessage();
            return reportResponse;
        }

        if(this.debug){
            System.out.println(output.toString());
        }

        Response response = httpPost("Query", output.toString());

        // Check if HTTP request is OK
        if(response.status){
            reportResponse.error = response.error;
            reportResponse.status = response.status;
            reportResponse.xml = response.xml;

            try {
                readReportResponse(response.xml, reportResponse);
            }
            catch (Exception e) {
                reportResponse.error = e.getMessage();
            }
        }

        return reportResponse;
    }

    /*
      * This command is issued by the client to cancel one or more previously
      * submitted short messages that are pending delivery.
      * */
    public Response cancel(long messageId){
        Response response = new Response ();

        Writer output = new StringWriter();
        XMLStreamWriter writer = startDocument("Cancel", output);

        if(writer == null){
            response.error = "Can not create XMLStreamWriter.";
            return response;
        }

        try {
            writeCredential(writer);
            writeMessageId(writer, messageId);
            endDocument(writer);
        } catch (Exception e) {
            response.error = e.getMessage();
            return response;
        }

        if(this.debug){
            System.out.println(output.toString());
        }

        response = httpPost("Cancel", output.toString());

        // Check if HTTP request is OK
        if(response.status){
            try {
                readResponse(response.xml, response);
            }
            catch (Exception e) {
                response.error = e.getMessage();
            }
        }

        return response;
    }

    /*
      * Get account settings/information
      * */
    public SettingsResponse getSettings(){
        SettingsResponse settingsResponse = new SettingsResponse();

        Writer output = new StringWriter();
        XMLStreamWriter writer = startDocument("GetSettings", output);

        if(writer == null){
            settingsResponse.error = "Can not create XMLStreamWriter.";
            return settingsResponse;
        }

        try {
            writeCredential(writer);
            endDocument(writer);
        } catch (Exception e) {
            settingsResponse.error = e.getMessage();
            return settingsResponse;
        }

        if(this.debug){
            System.out.println(output.toString());
        }

        Response response = httpPost("GetSettings", output.toString());

        // Check if HTTP request is OK
        if(response.status){
            settingsResponse.error = response.error;
            settingsResponse.status = response.status;
            settingsResponse.xml = response.xml;

            try {
                readSettingsResponse(response.xml, settingsResponse);
            }
            catch (Exception e) {
                settingsResponse.error = e.getMessage();
            }
        }

        return settingsResponse;
    }

    /*
      * XML chunk, Start XML document
      * */
    private XMLStreamWriter startDocument(String rootName, Writer output){
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(output);

            //writer.writeStartDocument();
            writer.writeStartElement(rootName);
            writer.writeAttribute("xmlns:i", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xmlns", "http://schemas.maradit.net/api/types");

            return writer;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
      * XML chunk, End XML document
      * */
    private void endDocument(XMLStreamWriter writer) throws Exception{
        writer.writeEndElement(); // rootName
        writer.writeEndDocument();
        writer.flush();
    }

    /*
      * XML chunk, User account info
      * */
    private void writeCredential(XMLStreamWriter writer) throws Exception{
        writer.writeStartElement("Credential");

        writer.writeStartElement("Password");
        writer.writeCharacters(this.password);
        writer.writeEndElement();

        writer.writeStartElement("Username");
        writer.writeCharacters(this.username);
        writer.writeEndElement();

        writer.writeEndElement(); // Credential
    }

    /*
      * XML chunk, SMS header info
      * */
    private void writeHeader(XMLStreamWriter writer) throws Exception{
        writer.writeStartElement("Header");

        writer.writeStartElement("From");
        writer.writeCharacters(this.from);
        writer.writeEndElement();

        writer.writeStartElement("ScheduledDeliveryTime");
        if(this.scheduledDeliveryTime == null){
            writer.writeAttribute("i:nil", "true");
            writer.writeCharacters("");
        }
        else{
            writer.writeCharacters(formatDate(this.scheduledDeliveryTime));
        }

        writer.writeEndElement();

        writer.writeStartElement("ValidityPeriod");
        writer.writeCharacters(String.valueOf(this.validityPeriod));
        writer.writeEndElement();

        writer.writeEndElement(); // Header
    }

    /*
      * XML chunk, Data coding
      * */
    private void writeDataCoding(XMLStreamWriter writer) throws Exception{
        writer.writeStartElement("DataCoding");
        writer.writeCharacters(this.dataCoding.toString());
        writer.writeEndElement();
    }

    /*
      * XML chunk, Message and cell phone numbers
      * */
    private void writeMessage(XMLStreamWriter writer, List<String> to, String message) throws Exception{
        writer.writeStartElement("Message");
        writer.writeCharacters(message);
        writer.writeEndElement();

        writer.writeStartElement("To");
        writer.writeAttribute("xmlns:d2p1", "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
        for (String msisdn : to) {
            writer.writeStartElement("d2p1:string");
            writer.writeCharacters(msisdn);
            writer.writeEndElement();
        }
        writer.writeEndElement(); // To
    }

    /*
      * XML chunk, Envelopes
      * */
    private void writeEnvelope(XMLStreamWriter writer, List<Envelope> envelopes) throws Exception{
        writer.writeStartElement("Envelopes");

        for (Envelope envelope : envelopes) {
            writer.writeStartElement("Envelope");

            writer.writeStartElement("Message");
            writer.writeCharacters(envelope.message);
            writer.writeEndElement();

            writer.writeStartElement("To");
            writer.writeCharacters(envelope.to);
            writer.writeEndElement();

            writer.writeEndElement();
        }

        writer.writeEndElement(); // Envelopes
    }

    /*
      * XML chunk, message id
      * */
    private void writeMessageId(XMLStreamWriter writer, long messageId) throws Exception{
        writer.writeStartElement("MessageId");
        writer.writeCharacters(String.valueOf(messageId));
        writer.writeEndElement();
    }

    /*
      * XML chunk, msisdn
      * */
    private void writeMSISDN(XMLStreamWriter writer, String msisdn) throws Exception{
        writer.writeStartElement("MSISDN");
        writer.writeCharacters(msisdn == null ? "" : msisdn);
        writer.writeEndElement();
    }

    /*
      * HTTP Helper
      * */
    private Response httpPost(String action, String content) {

        Response response = new Response();

        if(content == ""){
            response.error = "XML string is empty";
            return response;
        }

        try {
            // Create a socket to the host
            //int port = this.useHttps ? 443 : 80;
            InetAddress addr = InetAddress.getByName(this.url);
            Socket socket;

            if (this.useHttps) {
                SocketFactory socketFactory = SSLSocketFactory.getDefault();
                socket = socketFactory.createSocket(addr, 443);
            }
            else{
                socket = new Socket(addr, 80);
            }


            // Send header
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            wr.write("POST /api/xml/syncreply/" + action + " HTTP/1.1\r\n");
            wr.write("Host: " + this.url + "\r\n");
            wr.write("Content-Type: application/xml; charset=utf-8\r\n");
            wr.write("Content-Length: " + content.getBytes("UTF-8").length + "\r\n");
            wr.write("Connection: close\r\n");
            wr.write("\r\n");

            // Send data
            wr.write(content);
            wr.flush();

            // Get response
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line, data = "";
            while ((line = rd.readLine()) != null) {
                // Process line...
                data += line + "\r\n";
            }
            wr.close();
            rd.close();

            // Split into header and content
            String[] chunks = data.split("\r\n\r\n");

            if(chunks.length < 2){
                response.error = "Invalid response:\r\n" + data;
                return response;
            }

            String header = chunks[chunks.length - 2];
            response.xml = chunks[chunks.length - 1];

            String[] headers = header.split("\r\n");

            if(headers.length < 1){
                response.error = "Invalid header:\r\n" + header;
                return response;
            }
            else if(!headers[0].equalsIgnoreCase("HTTP/1.1 200 OK")){
                response.error  = "HTTP Status Code NOK\r\n";
                response.error += "\r\nResponse header:\r\n" + header;
                response.error += "\r\nResponse body:\r\n" + response.xml;
            }
            else{
                response.status = true;
            }
        }
        catch (Exception e) {
            response.error = e.getMessage();
        }

        return response;
    }

    /*
      * Read submit response XML
      * */
    private void readSubmitResponse(String xml, SubmitResponse submitResponse) throws Exception{
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = builder.parse(is);

            NodeList response = doc.getElementsByTagName("Response");

            if(response.getLength() == 1){
                Element element = (Element) response.item(0);

                Element tmp = (Element)element.getElementsByTagName("MessageId").item(0);
                submitResponse.messageId = Long.parseLong(getDataFromElement(tmp));

                tmp = (Element)element.getElementsByTagName("Code").item(0);
                submitResponse.statusCode = Integer.parseInt(getDataFromElement(tmp));

                tmp = (Element)element.getElementsByTagName("Description").item(0);
                submitResponse.statusDescription = getDataFromElement(tmp);
            }
        }
        catch (Exception e) {
            submitResponse.error = e.getMessage();
        }
    }

    /*
      * Read report response XML
      * */
    private void readReportResponse(String xml, ReportResponse reportResponse) throws Exception{
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = builder.parse(is);

            NodeList response = doc.getElementsByTagName("Response");

            if(response.getLength() == 1){
                Element element = (Element) response.item(0);

                Element tmp = (Element)element.getElementsByTagName("Code").item(0);
                reportResponse.statusCode = Integer.parseInt(getDataFromElement(tmp));

                tmp = (Element)element.getElementsByTagName("Description").item(0);
                reportResponse.statusDescription = getDataFromElement(tmp);

                if (reportResponse.statusCode == 200) {
                    NodeList reportNodes = element.getElementsByTagName("ReportDetailItem");
                    SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));

                    for (int i = 0; i < reportNodes.getLength(); i++) {
                        Element reportNode = (Element) reportNodes.item(i);
                        ReportDetail reportDetail = new ReportDetail();

                        tmp = (Element)reportNode.getElementsByTagName("Id").item(0);
                        reportDetail.id = Long.parseLong(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("Payload").item(0);
                        reportDetail.payload = getDataFromElement(tmp);

                        tmp = (Element)reportNode.getElementsByTagName("Xser").item(0);
                        reportDetail.xser = getDataFromElement(tmp);

                        tmp = (Element)reportNode.getElementsByTagName("Cost").item(0);
                        reportDetail.cost = Double.valueOf(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("LastUpdated").item(0);
                        reportDetail.lastUpdated = iso8601.parse(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("Submitted").item(0);
                        reportDetail.submitted = iso8601.parse(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("MSISDN").item(0);
                        reportDetail.msisdn = getDataFromElement(tmp);

                        tmp = (Element)reportNode.getElementsByTagName("Network").item(0);
                        reportDetail.network = Short.valueOf(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("Sequence").item(0);
                        reportDetail.sequence = Byte.valueOf(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("State").item(0);
                        reportDetail.state = State.valueOf(getDataFromElement(tmp));

                        tmp = (Element)reportNode.getElementsByTagName("ErrorCode").item(0);
                        reportDetail.errorCode = Integer.valueOf(getDataFromElement(tmp));

                        reportResponse.reportDetails.add(reportDetail);
                    }
                }
            }
        }
        catch (Exception e) {
            reportResponse.error = e.getMessage();
        }
    }

    /*
      * Read settings response XML
      * */
    private void readSettingsResponse(String xml, SettingsResponse settingsResponse) throws Exception{
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = builder.parse(is);

            NodeList response = doc.getElementsByTagName("Response");

            if(response.getLength() == 1){
                Element element = (Element) response.item(0);

                Element tmp = (Element)element.getElementsByTagName("Code").item(0);
                settingsResponse.statusCode = Integer.parseInt(getDataFromElement(tmp));

                tmp = (Element)element.getElementsByTagName("Description").item(0);
                settingsResponse.statusDescription = getDataFromElement(tmp);

                if (settingsResponse.statusCode == 200) {
                    SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));

                    NodeList nodes = element.getElementsByTagName("Balance");
                    if(nodes.getLength() == 1){
                        Element node = (Element) nodes.item(0);

                        tmp = (Element)node.getElementsByTagName("Limit").item(0);
                        settingsResponse.settings.balance.limit = Double.parseDouble(getDataFromElement(tmp));

                        tmp = (Element)node.getElementsByTagName("Main").item(0);
                        settingsResponse.settings.balance.main = Double.parseDouble(getDataFromElement(tmp));
                    }

                    nodes = element.getElementsByTagName("Keyword");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element node = (Element) nodes.item(i);
                        Settings.Keyword keyword = new Settings.Keyword();

                        tmp = (Element)node.getElementsByTagName("ServiceNumber").item(0);
                        keyword.serviceNumber = getDataFromElement(tmp);

                        tmp = (Element)node.getElementsByTagName("Timestamp").item(0);
                        keyword.timestamp = iso8601.parse(getDataFromElement(tmp));

                        tmp = (Element)node.getElementsByTagName("Validity").item(0);
                        keyword.validity = Short.valueOf(getDataFromElement(tmp));

                        tmp = (Element)node.getElementsByTagName("Value").item(0);
                        keyword.value = getDataFromElement(tmp);

                        settingsResponse.settings.keywords.add(keyword);
                    }

                    nodes = element.getElementsByTagName("Sender");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element node = (Element) nodes.item(i);
                        Settings.Sender sender = new Settings.Sender();

                        tmp = (Element)node.getElementsByTagName("Value").item(0);
                        sender.value = getDataFromElement(tmp);

                        tmp = (Element)node.getElementsByTagName("Default").item(0);
                        sender.isDefault = Boolean.parseBoolean(getDataFromElement(tmp));

                        settingsResponse.settings.senders.add(sender);
                    }
                }
            }
        }
        catch (Exception e) {
            settingsResponse.error = e.getMessage();
        }
    }

    /*
      * Read response XML
      * */
    private void readResponse(String xml, Response response) throws Exception{
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = builder.parse(is);

            NodeList responseNode = doc.getElementsByTagName("Response");

            if(responseNode.getLength() == 1){
                Element element = (Element) responseNode.item(0);

                Element tmp = (Element)element.getElementsByTagName("Code").item(0);
                response.statusCode = Integer.parseInt(getDataFromElement(tmp));

                tmp = (Element)element.getElementsByTagName("Description").item(0);
                response.statusDescription = getDataFromElement(tmp);
            }
        }
        catch (Exception e) {
            response.error = e.getMessage();
        }
    }

    /*
      * Get XML tag value
      * */
    public static String getDataFromElement(Element e) {
        Node child = e.getFirstChild();

        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }

        return "";
    }

    /*
      * Convert Date instance to string as ISO 8601 format
      * */
    private static String formatDate(Date date){
        DateFormat iso8601 = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");

        // format in (almost) ISO8601 format
        String dateStr = iso8601.format (date);

        // change time zone value from 0000 to 00:00 (starts at char 22)
        return dateStr.substring (0, 22) + ":" + dateStr.substring (22);
    }

}



