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
