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
package net.demo.hasor.domain.oauth;
import net.demo.hasor.manager.oauth.WeiboOAuth;
/**
 * 新浪微博 Token 信息
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class WeiboAccessInfo extends AccessInfo {
    private String accessToken = null;
    private String remind_in   = null;
    private long   expires_in  = 0;
    private String uid         = null;
    //
    //
    //
    public WeiboAccessInfo() {
        this.setProvider(WeiboOAuth.PROVIDER_NAME);
    }
    @Override
    public String getExternalUserID() {
        return this.getUid();
    }
    //
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRemind_in() {
        return remind_in;
    }
    public void setRemind_in(String remind_in) {
        this.remind_in = remind_in;
    }
    public long getExpires_in() {
        return expires_in;
    }
    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
}