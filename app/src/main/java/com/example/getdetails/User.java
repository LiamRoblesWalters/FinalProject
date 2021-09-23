package com.example.getdetails;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.nio.file.Path;
@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;

    public String email;
    public Address address;
    public String imageUri = "";
    public Address getAddress() { return address; }

    public User(String name) {
        this.name = name;
        this.email = "";
        this.address = new Address("street", new Geo("0", "0"));
    }

    public void replace(User user){
        this.name = user.name;
        this.email = user.email;
        this.imageUri = user.imageUri;
        this.address = user.address;

    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

class Address {

    public String street;
    public Geo geo;

    public Address(String street, Geo geo) {
        this.street = street;
        this.geo = geo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }


}
class Geo{
    public String lat;
    public String lng;

    public Geo(String lat, String lng){
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    @Override
    public String toString() {
        return "Geo{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
