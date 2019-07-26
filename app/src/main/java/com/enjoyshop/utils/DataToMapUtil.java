package com.enjoyshop.utils;

import java.util.HashMap;
import java.util.Map;

public class DataToMapUtil {

    public static Map toMap(String... args){
        Map<String,Object> map = new HashMap<>();
        if(args!=null) {
            for(String arg:args) {
                map.put(arg,arg);
            }
        }
        return map;
    }
}
