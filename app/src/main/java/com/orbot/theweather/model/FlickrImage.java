package com.orbot.theweather.model;

/**
 * Created by Rahul on 18/11/2015.
 */
public class FlickrImage {
    private int   serverID, farmID;
    private String owner, secret, title;
    private boolean isPublic, isFriend, isFamily;

    private String ID;

    public FlickrImage() {
        this.ID = "";
        this.serverID = 0;
        this.farmID = 0;
        this.owner = "";
        this.secret = "";
        this.title = "";
        this.isPublic = false;
        this.isFriend = false;
        this.isFamily = false;
    }

    public FlickrImage(String ID, int serverID, int farmID, String owner, String secret,
                       String title, boolean isPublic, boolean isFriend, boolean isFamily) {
        this.ID = ID;
        this.serverID = serverID;
        this.farmID = farmID;
        this.owner = owner;
        this.secret = secret;
        this.title = title;
        this.isPublic = isPublic;
        this.isFriend = isFriend;
        this.isFamily = isFamily;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public int getFarmID() {
        return farmID;
    }

    public void setFarmID(int farmID) {
        this.farmID = farmID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isFamily() {
        return isFamily;
    }

    public void setIsFamily(boolean isFamily) {
        this.isFamily = isFamily;
    }

    @Override
    public String toString() {
        return "FlickrImage{" +
                "ID=" + ID +
                ", serverID=" + serverID +
                ", farmID=" + farmID +
                ", owner='" + owner + '\'' +
                ", secret='" + secret + '\'' +
                ", title='" + title + '\'' +
                ", isPublic=" + isPublic +
                ", isFriend=" + isFriend +
                ", isFamily=" + isFamily +
                '}';
    }
}
