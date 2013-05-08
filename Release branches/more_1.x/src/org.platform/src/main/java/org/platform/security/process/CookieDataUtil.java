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
package org.platform.security.process;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
/**
 * π§æﬂ¿‡
 * @version : 2013-4-24
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class CookieDataUtil {
    public static CookieDataUtil parseJson(String jsonString) {
        List<CookieUserData> cookieUserList = JSON.parseArray(jsonString, CookieUserData.class);
        CookieDataUtil data = new CookieDataUtil();
        if (cookieUserList != null)
            for (CookieUserData userData : cookieUserList)
                data.addCookieUserData(userData);
        return data;
    }
    public static String parseString(CookieDataUtil cookieData) {
        if (cookieData == null)
            return "[]";
        return JSON.toJSONString(cookieData.cookieUserDataMap.values());
    }
    public static CookieDataUtil create() {
        return new CookieDataUtil();
    }
    private Map<String, CookieUserData> cookieUserDataMap = new HashMap<String, CookieUserData>();
    public void addCookieUserData(CookieUserData cookieUserData) {
        this.cookieUserDataMap.put(cookieUserData.getUserCode(), cookieUserData);
    }
    public CookieUserData[] getCookieUserDatas() {
        return this.cookieUserDataMap.values().toArray(new CookieUserData[this.cookieUserDataMap.size()]);
    }
    public final static class CookieUserData {
        private String userCode     = null;
        private String authSystem   = null;
        private long   appStartTime = 0;
        //
        public String getUserCode() {
            return userCode;
        }
        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }
        public String getAuthSystem() {
            return authSystem;
        }
        public void setAuthSystem(String authSystem) {
            this.authSystem = authSystem;
        }
        public long getAppStartTime() {
            return appStartTime;
        }
        public void setAppStartTime(long appStartTime) {
            this.appStartTime = appStartTime;
        }
    }
}