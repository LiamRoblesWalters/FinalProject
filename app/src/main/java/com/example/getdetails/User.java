package com.example.getdetails;

import android.net.Uri;

import java.nio.file.Path;

public class User {
    //public int id;
    public String name;
    public String email;
    public Address address;
    public String imageUri = "";

    public Address getAddress() { return address; }

    public User(String name) {
        this.name = name;
        this.email = "";
        this.address = new Address("");
    }

}
class Address {

    public String street;

    public Address(String street) {
        this.street = street;
    }
}
