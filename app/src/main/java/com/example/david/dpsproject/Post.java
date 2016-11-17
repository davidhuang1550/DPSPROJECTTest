package com.example.david.dpsproject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by david on 2016-10-28.
 */
public class Post implements Serializable {

    private int No;
    private int Yes;
    private String PosterId;
    private String Title;
    private String key;
    private String Description;

    private String Image;
    private ArrayList<Comment> comments;
    Post(){

    }

    Post(String key, int no, int yes, String posterId, String title, ArrayList<Comment> comments, String description,String image){

        this.key = key;
        No = no;
        Yes = yes;
        PosterId = posterId;
        Title = title;
        this.comments = comments;
        Description = description;
        Image=image;
    }
    Post(String posterId,String title,String description){
        Yes=0;No=0;
        PosterId=posterId;
        Title=title;
        Description=description;
    }
    Post(String posterId,String title,String description,String image){
        Yes=0;No=0;
        PosterId=posterId;
        Title=title;
        Description=description;
        Image=image;
    }
    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }


    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public int getYes() {
        return Yes;
    }

    public void setYes(int yes) {
        Yes = yes;
    }

    public String getPosterId() {
        return PosterId;
    }

    public void setPosterId(String posterId) {
        PosterId = posterId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void IncNo(){
        No++;
    }
    public void IncYes(){
        Yes++;
    }

}
