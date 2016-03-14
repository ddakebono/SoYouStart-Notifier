/*
 * Copyright (c) 2016 Owen Bennett.
 *  You may use, distribute and modify this code under the terms of the MIT licence.
 *  You should have obtained a copy of the MIT licence with this software,
 *  if not please obtain one from https://opensource.org/licences/MIT
 *
 *
 *
 */

package org.frostbite.sysnotify;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class Notify {
    public static String UPDATE_URL = "https://ws.ovh.ca/dedicated/r2/ws.dispatcher/getAvailability2";
    public static boolean murder = false;
    public static void main(String[] args){
        Logger log = LoggerFactory.getLogger(Notify.class);
        log.info("Starting SoYouStart Notifier!");
        Configuration cfg = new Configuration();
        try {
            cfg.initConfig(log);
        } catch (IOException e) {
            log.trace(e.getMessage());
            log.info("Error occur with configuration reading. Please check the config and retry the program.");
            System.exit(1);
        }
        try {
            notifyRunner(cfg, log);
        } catch (IOException e) {
            log.trace(e.getMessage());
        }
    }
    public static void notifyRunner(Configuration cfg, Logger log) throws IOException {
        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser();
        URL url = new URL(UPDATE_URL);
        URLConnection getData;
        BufferedReader in;
        String temp;
        String temp2;
        Map siteData;
        ArrayList<Map> finalWatched = new ArrayList<>();
        while(!murder){
            temp2 = "";
            finalWatched.clear();
            getData = url.openConnection();
            in = new BufferedReader(new InputStreamReader(getData.getInputStream()));
            while((temp = in.readLine()) != null){
                temp2 += temp;
            }
            siteData = parser.parseJson(temp2);
            Map avail = (Map)siteData.get("answer");
            ArrayList<Map> avail2 = (ArrayList)avail.get("availability");
            for(Map watchCheck : avail2){
                for(WatchedServer server : cfg.getServers()){
                    if(((String)watchCheck.get("reference")).equalsIgnoreCase(server.getServerID())){
                        finalWatched.add(watchCheck);
                    }
                }
            }
            checkForChanges(cfg, finalWatched, log);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                log.trace(e.getMessage());
            }
        }
    }
    public static void checkForChanges(Configuration cfg, ArrayList<Map> finalWatched, Logger log){
        ArrayList<WatchedServer> hasChanged = new ArrayList<>();
        for(Map check : finalWatched){
            for(int i=0; i<cfg.getServers().size(); i++){
                if(((String)check.get("reference")).equalsIgnoreCase(cfg.getServers().get(i).getServerID())){
                    for(Map zone : (ArrayList<Map>)check.get("zones")){
                        if(((String)zone.get("zone")).equalsIgnoreCase(cfg.getServers().get(i).getZone())){
                            if(!((String)zone.get("availability")).equalsIgnoreCase("unavailable") && !cfg.getServers().get(i).isAvailable()){
                                cfg.getServers().get(i).setAvailable(true);
                                hasChanged.add(cfg.getServers().get(i));
                            } else if(((String)zone.get("availability")).equalsIgnoreCase("unavailable") &&  cfg.getServers().get(i).isAvailable()){
                                cfg.getServers().get(i).setAvailable(false);
                                hasChanged.add(cfg.getServers().get(i));
                            }
                        }
                    }
                }
            }
        }
        if(hasChanged.size() > 0) {
            boolean send = false;
            String msgBuild = "The following servers are now available!\n\n";
            for (WatchedServer server : hasChanged) {
                if (server.isAvailable()) {
                    msgBuild += "Server " + server.getServerID() + " in zone " + server.getZone() + " is now available for rental!\n";
                    send = true;
                } else {
                    log.info("Server " + server.getServerID() + " is now unavailable.");
                }
            }
            if(send)
                sendMail(msgBuild, cfg, log);
        } else {
            log.info("No change in server availability!");
        }
    }
    public static void sendMail(String msg, Configuration cfg, Logger log) {
        System.out.println(msg);
        Properties props = new Properties();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", cfg.getSmtpHostname());
        props.put("mail.smtp.auth", cfg.isSmtpAuthEnabled());
        props.put("mail.smtp.port", cfg.getSmtpPort());
        props.put("mail.smtp.user", cfg.getSmtpUsername());
        props.put("mail.smtp.password", cfg.getSmtpPassword());
        props.put("mail.smtp.starttls.enable", true);
        Session session = Session.getInstance(props, null);
        Message message = new MimeMessage(session);
        try {
            log.debug("Sending mail to " + cfg.getTargetEmail());
            message.setFrom(new InternetAddress(cfg.getSmtpUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cfg.getTargetEmail()));
            message.setSubject("SoYouStart Automated Server Availability Update");
            message.setText(msg + "\n\nhttp://www.soyoustart.com/ca/en/essential-servers/\n\n" + df.format(date));
            Transport transport = session.getTransport("smtp");
            transport.connect(cfg.getSmtpHostname(), cfg.getSmtpUsername(), cfg.getSmtpPassword());
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            log.trace(e.getMessage());
        }
        log.info("Done sending!");
        //System.exit(0);
    }

}