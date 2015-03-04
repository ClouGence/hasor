package net.hasor.little.db;

import com.alibaba.fastjson.JSON;

/**
 * Created by Administrator on 15-3-4.
 */
public class JsonBridge {
    public static String toJson(Object obj){
        return JSON.toJSONString(obj);
    }
}
