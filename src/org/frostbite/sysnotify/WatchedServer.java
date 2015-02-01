package org.frostbite.sysnotify;

public class WatchedServer {
    private String serverID;
    private String zone;
    private boolean isAvailable;
    public WatchedServer(String serverID, String zone){
        this.serverID = serverID;
        this.zone = zone;
    }

    public String getServerID() {
        return serverID;
    }

    public String getZone() {
        return zone;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
