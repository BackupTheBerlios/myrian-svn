package com.redhat.persistence.jdo;

import java.util.HashMap;
import java.util.Map;

public class Magazine {
    private int id;
    private String title;
    private Map index;

    public Magazine() {}

    public Magazine(int id) {
        this.id = id;
        index = new HashMap();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map getIndex() {
        return index;
    }

    public void setMap(Map index) {
        this.index = index;
    }

    public String toString() {
        return "<magazine #" + id + ">";
    }
}
