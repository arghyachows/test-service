package com.gintaa.hlrc.model;

public class User {

    private String _id;
    private String username;
    private String password;

    public User() {
    }

    public User(String _id, String username, String password) {
        this._id = _id;
        this.username = username;
        this.password = password;
    }

    public String get_id() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User _id(String _id) {
        this._id = _id;
        return this;
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "{" + " _id='" + get_id() + "'" + ", username='" + getUsername() + "'" + ", password='" + getPassword()
                + "'" + "}";
    }

}