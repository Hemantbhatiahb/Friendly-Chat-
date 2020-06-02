package com.example.firebasefeatures;

public class InstantMessage {

    String author ;
    String message ;

    public InstantMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }

    public InstantMessage() {
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }
}
