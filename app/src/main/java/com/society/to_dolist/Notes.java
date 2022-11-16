package com.society.to_dolist;

public class Notes {
    String Title;
    String Desc;
    String UID;

    public Notes(){}
    public Notes(String mTitle, String mDesc, String UID){
        this.Title = mTitle;
        this.Desc = mDesc;
        this.UID = UID;
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
