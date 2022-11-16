package com.society.to_dolist;

public class Done {
    String Title;
    String Desc;
    String UID;


    public Done(){}
    public Done(String mTitle, String mDesc, String mUID){
        this.Title = mTitle;
        this.Desc = mDesc;
        this.UID = mUID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
