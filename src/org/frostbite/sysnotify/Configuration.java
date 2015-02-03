package org.frostbite.sysnotify;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Configuration {
    private String targetEmail;
    private String smtpHostname;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private boolean smtpAuthEnabled;
    private String watchedServersNotSplit;
    private String[] watchedServers;
    private ArrayList<WatchedServer> servers = new ArrayList<>();

    public void initConfig(Logger log) throws IOException {
        boolean isInit = true;
        Properties cfg = new Properties();
        File check = new File("conf/config.prop");
        if(check.isFile()){
            cfg.load(new FileInputStream("conf/config.prop"));
        } else {
            isInit = false;
            boolean conf = new File("conf").mkdirs();
            if(!conf) {
                log.info("CONFIGURATION FOLDER COULD NOT BE CREATED!");
                log.info("Shutting down notifier due to error.");
                System.exit(1);
            }
        }
        targetEmail = cfg.getProperty("TargetEmail", "change@me.now");
        smtpHostname = cfg.getProperty("SMTPHostname", "localhost");
        smtpPort = cfg.getProperty("SMTPPort", "587");
        smtpUsername = cfg.getProperty("SMTPUsername", "change@me.now");
        smtpPassword = cfg.getProperty("SMTPPassword", "changeme");
        smtpAuthEnabled = Boolean.parseBoolean(cfg.getProperty("SMTPUseAuthentication", "true"));
        watchedServersNotSplit = cfg.getProperty("WatchedServerID", "143casys1:bhs, 143casys2:bhs, 143casys3:bhs, serverid:zone");
        watchedServers = watchedServersNotSplit.split(",\\s?");
        if(!isInit){
            log.info("Creating new configuration file");
            mkNewConfig(log);
        }
        for(String split : watchedServers){
            String[] temp = split.split(":");
            servers.add(new WatchedServer(temp[0], temp[1]));
            log.info("Added server: " + servers.get(servers.size() - 1).getServerID() + " in zone: " + servers.get(servers.size() - 1).getZone());
        }
    }
    public void mkNewConfig(Logger log) throws IOException {
        Properties cfg = new Properties();
        String comment = "So you Start server notifier configuration";
        cfg.setProperty("TargetEmail", targetEmail);
        cfg.setProperty("SMTPHostname", smtpHostname);
        cfg.setProperty("SMTPPort", smtpPort);
        cfg.setProperty("SMTPUsername", smtpUsername);
        cfg.setProperty("SMTPPassword", smtpPassword);
        cfg.setProperty("WatchedServerID", watchedServersNotSplit);
        cfg.setProperty("SMTPUseAuthentication", String.valueOf(smtpAuthEnabled));
        cfg.store(new FileOutputStream("conf/config.prop"), comment);
        log.info("Config file generated, please configure as needed. Then rerun this program.");
        System.exit(0);
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public String getSmtpHostname() {
        return smtpHostname;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public boolean isSmtpAuthEnabled() {
        return smtpAuthEnabled;
    }

    public ArrayList<WatchedServer> getServers() {
        return servers;
    }
}
