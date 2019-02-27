package com.project.myapp;

import android.util.Log;

public class LocationModel {
    public LocationModel(){

    }
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;

    public void setLat1(double mLat1){
        this.lat1 = mLat1;
    }
    public void setLat2(double mlat2){
        this.lat2 = mlat2;
    }
    public void setLon1(double mlon1){
        this.lon1 = mlon1;
    }
    public void setLon2(double mlon2){
        this.lon2 = mlon2;
    }
    public double getLat1(){
        return this.lat1;
    }
    public double getLat2(){
        return this.lat2;
    }
    public double getLon1(){
        return this.lon1;
    }
    public double getLon2(){
        return this.lon2;
    }
    public double get_distance_in_meter(){
//        double R = 6371000 ; // Radius of the earth in m 6371000f
//        double dLat = (lat1 - lat2) * Math.PI / 180f;
//        double dLon = (lon1 - lon2) * Math.PI / 180f;
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(lat1 * (Math.PI / 180f)) * Math.cos(lat2 * (Math.PI / 180f)) *
//                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        double c = 2f * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double d = R * c;
//        return d;

        double theta = this.lon1 - this.lon2;
        double dist = Math.sin(deg2rad(this.lat1))
                * Math.sin(deg2rad(this.lat2))
                + Math.cos(deg2rad(this.lat1))
                * Math.cos(deg2rad(this.lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.e("distance_in_meter",">>> "+ String.valueOf(dist * 1000));
//        return dist * 1000;

        return (double) MainActivity.distance_in_meter2((float) this.lat1, (float)this.lon1,  (float)this.lat2,  (float)this.lon2);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
