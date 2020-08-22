package com.android.messaging.ui.conversation;

public class DanmuComments {
    private String name;
    private String content;

    DanmuComments(String name, String content){
        this.name = name;
        this.content = content;
    }

    public String getName(){
        return name;
    }

    public String getContent(){
        return content;
    }
}
