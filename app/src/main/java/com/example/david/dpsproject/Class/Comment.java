package com.example.david.dpsproject.Class;

import java.io.Serializable;

/**
 * Created by david on 2016-11-03.
 */
public class Comment implements Serializable {
    public String getCommentPosterId() {
        return CommentPosterId;
    }

    public void setCommentPosterId(String commentPosterId) {
        CommentPosterId = commentPosterId;
    }

    public String getCommentDesc() {
        return CommentDesc;
    }

    public void setCommentDesc(String commentDesc) {
        CommentDesc = commentDesc;
    }

    private String CommentPosterId;
    private String CommentDesc;

    public Comment() {

    }

    public Comment(String commentPosterId, String commentDesc) {
        CommentPosterId = commentPosterId;
        CommentDesc = commentDesc;
    }
}
