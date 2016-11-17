package com.example.david.dpsproject;

/**
 * Created by david on 2016-10-23.
 */
public class Users {

    private String userName;
    private String password;

    private Profile profile;

    public  Users(){
        profile = new Profile();
    }
    public Users(String u, String p,Profile pro) {
        userName = u;
        password = p;
        profile=pro;
    }

    public Users Users(Users users){

        Users u = new Users();
        u.setProfile(users.getProfile());
        u.setPassword(users.getPassword());
        u.setUserName(users.getUserName());

        return u;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
