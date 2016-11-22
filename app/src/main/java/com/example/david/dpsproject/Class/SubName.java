package com.example.david.dpsproject.Class;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-19.
 */
public class SubName {
    private ArrayList<Posts> Sub;

    public SubName(){
        Sub = new ArrayList<Posts>();
    }
    public SubName(ArrayList<Posts>p){
        Sub=p;
    }
    public void addSub(ArrayList<String> pt, String s){
        Posts p = new Posts(pt,s);
        Sub.add(p);
    }
    public ArrayList<Posts> getSub() {
        return Sub;
    }

    public void setSub(ArrayList<Posts> subName) {
        Sub=subName;
    }


}
