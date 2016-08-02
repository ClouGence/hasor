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
package net.demo.hasor.manager.oauth;
import net.hasor.core.Init;
import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 *
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class TencentOAuthConfig extends AbstractOAuthConfig {
    //QQ登录接入,授权key
    @InjectSettings("tencent.qqAdmins")
    private String adminsCode    = null;
    //应用ID
    @InjectSettings("tencent.app_id")
    private String appID         = null;
    //应用Key
    @InjectSettings("tencent.app_key")
    private String appKey        = null;
    //权限
    @InjectSettings("tencent.oauth_scope")
    private String scope         = null;
    private String finalLoginURL = null;
    //
    @Init
    public void init() throws UnsupportedEncodingException {
        //  https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=[YOUR_APPID]&redirect_uri=[YOUR_REDIRECT_URI]&scope=[THE_SCOPE]
        this.finalLoginURL = "https://graph.qq.com/oauth2.0/authorize?response_type=code" //
                + "&client_id=" + this.appID //
                + "&redirect_uri=" + URLEncoder.encode(this.getRedirectURI() + "&provider=qq", "utf-8") //
                + "&scope=" + this.scope;//
    }
    //
    @Override
    public String getLoginURL() {
        return this.finalLoginURL;
    }
    //
    public String getAdmins() {
        return this.adminsCode;
    }
}