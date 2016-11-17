package com.example.david.dpsproject;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-07.
 */
public class Sub {


    ArrayList<Post> posts;

    Sub(){
        posts= new ArrayList<>();
    }
    Sub(ArrayList<Post> p){
        posts=p;
    }

    public void pushPost(Post p){
        posts.add(p);
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
