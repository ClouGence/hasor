/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.test.db;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @version : 2013-12-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestUtils {
    public static final  String   INSERT_ARRAY = "insert into tb_user (userUUID,name,loginName,loginPassword,email,index,registerTime) values (?,?,?,?,?,?,?);";
    public static final  String   INSERT_MAP   = "insert into tb_user (userUUID,name,loginName,loginPassword,email,index,registerTime) values (:userUUID,:name,:loginName,:loginPassword,:email,:index,:registerTime);";
    private static final Object[] DATA_1       = new Object[] { newID(), "默罕默德", "muhammad", "1", "muhammad@hasor.net", 1, new Date() };
    private static final Object[] DATA_2       = new Object[] { newID(), "安妮.贝隆", "belon", "2", "belon@hasor.net", 2, new Date() };
    private static final Object[] DATA_3       = new Object[] { newID(), "赵飞燕", "feiyan", "3", "feiyan@hasor.net", 3, new Date() };
    private static final Object[] DATA_4       = new Object[] { newID(), "赵子龙", "zhaoyun", "4", "zhaoyun@hasor.net", 4, new Date() };
    private static final Object[] DATA_5       = new Object[] { newID(), "诸葛亮", "wolong", "5", "wolong@hasor.net", 5, new Date() };
    private static final Object[] DATA_6       = new Object[] { newID(), "张果老", "guolao", null, "guolao@hasor.net", 6, new Date() };
    private static final Object[] DATA_7       = new Object[] { newID(), "吴广", "wuguang", null, "wuguang@hasor.net", 7, new Date() };

    private static String newID() {
        return UUID.randomUUID().toString();
    }

    private static TB_User fillBean(Object[] data, TB_User tbUser) {
        tbUser.setUserUUID((String) data[0]);
        tbUser.setName((String) data[1]);
        tbUser.setLoginName((String) data[2]);
        tbUser.setLoginPassword((String) data[3]);
        tbUser.setEmail((String) data[4]);
        tbUser.setIndex((Integer) data[5]);
        tbUser.setRegisterTime((Date) data[6]);
        return tbUser;
    }

    private static Map<String, Object> fillMap(Object[] data, Map<String, Object> map) {
        map.put("userUUID", data[0]);
        map.put("name", data[1]);
        map.put("loginName", data[2]);
        map.put("loginPassword", data[3]);
        map.put("email", data[4]);
        map.put("index", data[5]);
        map.put("registerTime", data[6]);
        return map;
    }

    public static TB_User beanForData1() {
        return fillBean(DATA_1, new TB_User());
    }

    public static Object[] arrayForData1() {
        return DATA_1;
    }

    public static Map<String, Object> mapForData1() {
        return fillMap(DATA_1, new HashMap<>());
    }

    public static TB_User beanForData2() {
        return fillBean(DATA_2, new TB_User());
    }

    public static Object[] arrayForData2() {
        return DATA_2;
    }

    public static Map<String, Object> mapForData2() {
        return fillMap(DATA_2, new HashMap<>());
    }

    public static TB_User beanForData3() {
        return fillBean(DATA_3, new TB_User());
    }

    public static Object[] arrayForData3() {
        return DATA_3;
    }

    public static Map<String, Object> mapForData3() {
        return fillMap(DATA_3, new HashMap<>());
    }

    public static TB_User beanForData4() {
        return fillBean(DATA_4, new TB_User());
    }

    public static Object[] arrayForData4() {
        return DATA_4;
    }

    public static Map<String, Object> mapForData4() {
        return fillMap(DATA_4, new HashMap<>());
    }

    public static TB_User beanForData5() {
        return fillBean(DATA_5, new TB_User());
    }

    public static Object[] arrayForData5() {
        return DATA_5;
    }

    public static Map<String, Object> mapForData5() {
        return fillMap(DATA_5, new HashMap<>());
    }

    public static TB_User beanForData6() {
        return fillBean(DATA_6, new TB_User());
    }

    public static Object[] arrayForData6() {
        return DATA_6;
    }

    public static Map<String, Object> mapForData6() {
        return fillMap(DATA_6, new HashMap<>());
    }

    public static TB_User beanForData7() {
        return fillBean(DATA_7, new TB_User());
    }

    public static Object[] arrayForData7() {
        return DATA_7;
    }

    public static Map<String, Object> mapForData7() {
        return fillMap(DATA_7, new HashMap<>());
    }
}