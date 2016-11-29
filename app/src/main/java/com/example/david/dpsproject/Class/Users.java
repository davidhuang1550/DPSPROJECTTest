package com.example.david.dpsproject.Class;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by david on 2016-10-23.
 */
public class Users {

    private String userName;
    private String password;

    private String Picture;


    private HashMap<String,ArrayList<String>> Posts;
    private HashMap<String,ArrayList<String>> Bookmarks;
    private HashMap<String,ArrayList<String>> Viewed;

    private ArrayList<String> Subcategory;
    //private Profile profile;

  //  private ArrayList<Posts> posts;


    public  Users(){
     //   profile = new Profile();
        Posts=new HashMap<String,ArrayList<String>>();
        Bookmarks=new HashMap<String,ArrayList<String>>();
        Viewed=new HashMap<String,ArrayList<String>>();
    }
    public Users(String u, String p,HashMap<String,ArrayList<String>> s,HashMap<String,ArrayList<String>> b, HashMap<String,ArrayList<String>> v, String pic,ArrayList<String> subc) {
        userName = u;
        password = p;
        Posts=s;
        Bookmarks=b;
        Viewed=v;
        Picture=pic;
        Subcategory=subc;
      //  profile=pro;
    }

    public Users Users(Users users){

        Users u = new Users();
        //u.setProfile(users.getProfile());
        u.setPassword(users.getPassword());
        u.setUserName(users.getUserName());

        return u;
    }
    public ArrayList<String> getSubcategory() {
        return Subcategory;
    }

    public void setSubcategory(ArrayList<String> subcategory) {
        Subcategory = subcategory;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }
    public HashMap<String, ArrayList<String>> getPosts() {
        return Posts;
    }

    public void setPosts(HashMap<String, ArrayList<String>> posts) {
        Posts = posts;
    }

    public HashMap<String,ArrayList<String>> getBookmarks() {
        return Bookmarks;
    }

    public void setBookmarks(HashMap<String,ArrayList<String>> bookmarks) {
        Bookmarks = bookmarks;
    }

    public HashMap<String,ArrayList<String>> getViewed() {
        return Viewed;
    }

    public void setViewed(HashMap<String,ArrayList<String>> viewed) {
        Viewed = viewed;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addSub(String s){
        Subcategory.add(s);
    }
    public void deleteSub(String s){
        for(String temp:Subcategory){
            if(temp.equals(s)){
                Subcategory.remove(s);
            }
        }
    }

}
