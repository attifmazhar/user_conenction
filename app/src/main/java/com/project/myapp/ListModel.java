package com.project.myapp;

import android.graphics.Bitmap;

public class ListModel {
    private Bitmap profilePic;
    private String nickName;
    private String nationality;
    private Bitmap nationPic;
    private String profilePicUrl;
    private boolean isOutOfRange;
    private String location;
    private String deviceAddress;
    private boolean visibility;
    private String connectionState;
    private String positionState;
    private String permissionState;
    private String appState;
    private Long lastUpdateDate;
    private double distance ;
    private boolean userVisibility;

    public boolean getVisibility() {
        return true;//visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isUserVisibility() {
        return true;//userVisibility;
    }

    public void setUserVisibility(boolean userVisibility) {
        this.userVisibility = userVisibility;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    //NEW CODE
    private String macID;

    public boolean isOutOfRange() {
        return isOutOfRange;
    }

    //a-comment
    public void setOutOfRange(boolean outOfRange) {
        isOutOfRange = outOfRange;
    }

    public String getMacID() {
        return macID;
    }

    public void setMacID(String macID) {
        this.macID = macID;
    }

    //NEW CODE
    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public Bitmap getNationPic() {
        return nationPic;
    }

    public void setNationPic(Bitmap nationPic) {
        this.nationPic = nationPic;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getConnectionState() {
        return connectionState;
    }

    //a-comment
    public void setConnectionState(String connectionState) {
        this.connectionState = connectionState;
    }

    public String getPositionState() {
        return positionState;
    }

    public void setPositionState(String positionState) {
        this.positionState = positionState;
    }

    public String getPermissionState() {
        return permissionState;
    }

    public void setPermissionState(String permissionState) {
        this.permissionState = permissionState;
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState =  appState;
    }

    public Long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    String mac;
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getMac() {
        return this.mac;
    }
}
