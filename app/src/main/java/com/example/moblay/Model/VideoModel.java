package com.example.moblay.Model;

public class VideoModel {

    String str_path, str_thumb; //video path and video thumbnail
    boolean boolean_selected;

    public String getStr_path() {
        return str_path;
    }

    public void setStr_path(String str_path) {
        this.str_path = str_path;
    }

    public String getStr_thumb() {
        return str_thumb;
    }

    public void setStr_thumb(String str_thumb) {
        this.str_thumb = str_thumb;
    }

    public void setBoolean_selected(boolean boolean_selected) {
        this.boolean_selected = boolean_selected;
    }

    public boolean equals (Object obj)
    {
        if (this == obj) return true;
        if (this == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        // Class name is Employ & have lastname
        VideoModel vm = (VideoModel) obj ;
        return this.str_path.equals(vm.getStr_path());
    }
}
