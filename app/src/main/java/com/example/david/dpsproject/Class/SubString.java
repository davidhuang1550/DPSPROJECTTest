package com.example.david.dpsproject.Class;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-16.
 */
public class SubString {
   // private String subName;
    private ArrayList<String> posts;

    public SubString(){
        posts= new ArrayList<String>();
    }
    public SubString(ArrayList<String> p){
        posts=p;
     //   subName=s;
    }
    public ArrayList<String> getPosts() {
        return posts;
    }

    public void addPost(String P){
        posts.add(P);
    }
    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }

  //  public String getsubName() {
    //    return subName;
  //  }

   // public void setsubName(String subName) {
     //   subName = subName;
 //   }

}
