package com.enjoyshop.helper;

import java.io.Serializable;
import java.util.HashMap;

public class MyMap implements Serializable {
    private HashMap<String,Object> map;

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }

    public HashMap<String, Object> getMap() {
        return map;
    }
}
