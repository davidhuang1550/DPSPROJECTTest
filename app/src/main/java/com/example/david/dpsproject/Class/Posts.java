package com.example.david.dpsproject.Class;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-19.
 */
public class Posts {

    private ArrayList<String> post;
    private String sName;

    public Posts(){
        post=new ArrayList<String>();
    }
    public Posts(ArrayList<String>p,String s){
        post=p;
        sName=s;
    }
    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsName() {
        return sName;
    }
    public void addPost(String p){
        post.add(p);
    }

    public ArrayList<String> getPost() {
        return post;
    }

    public void setPost(ArrayList<String> post) {
        this.post = post;
    }


}
