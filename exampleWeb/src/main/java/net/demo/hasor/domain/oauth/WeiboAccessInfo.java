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
    private String  accessToken        = null;
    private String  remind_in          = null;
    private long    expires_in         = 0;
    private String  accessUserID       = null;
    //
    private long    id                 = 0;    // 用户UID
    private String  idstr              = null; // 字符串型的用户UID
    private String  screen_name        = null; // 用户昵称
    private String  name               = null; // 友好显示名称
    private int     province           = 0;    // 用户所在省级ID
    private int     city               = 0;    // 用户所在城市ID
    private String  location           = null; // 用户所在地
    private String  description        = null; // 用户个人描述
    private String  url                = null; // 用户博客地址
    private String  profile_image_url  = null; // 用户头像地址（中图），50×50像素
    private String  profile_url        = null; // 用户的微博统一URL地址
    private String  domain             = null; // 用户的个性化域名
    private String  weihao             = null; // 用户的微号
    private String  gender             = null; // 性别，m：男、f：女、n：未知
    private int     followers_count    = 0;    // 粉丝数
    private int     friends_count      = 0;    // 关注数
    private int     statuses_count     = 0;    // 微博数
    private int     favourites_count   = 0;    // 收藏数
    private String  created_at         = null; // 用户创建（注册）时间
    private Boolean following          = null; // 暂未支持
    private Boolean allow_all_act_msg  = null; // 是否允许所有人给我发私信，true：是，false：否
    private Boolean geo_enabled        = null; // 是否允许标识用户的地理位置，true：是，false：否
    private Boolean verified           = null; // 是否是微博认证用户，即加V用户，true：是，false：否
    private int     verified_type      = 0;    // 暂未支持
    private String  remark             = null; // 用户备注信息，只有在查询用户关系时才返回此字段
    private Boolean allow_all_comment  = null; // 是否允许所有人对我的微博进行评论，true：是，false：否
    private String  avatar_large       = null; // 用户头像地址（大图），180×180像素
    private String  avatar_hd          = null; // 用户头像地址（高清），高清头像原图
    private String  verified_reason    = null; // 认证原因
    private Boolean follow_me          = null; // 该用户是否关注当前登录用户，true：是，false：否
    private int     online_status      = 0;    // 用户的在线状态，0：不在线、1：在线
    private int     bi_followers_count = 0;    // 用户的互粉数
    private String  lang               = null; // 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语
    //
    //
    //
    public WeiboAccessInfo() {
        this.setProvider(WeiboOAuth.PROVIDER_NAME);
    }
    @Override
    public String getExternalUserID() {
        return this.getIdstr();
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
    public String getAccessUserID() {
        return accessUserID;
    }
    public void setAccessUserID(String accessUserID) {
        this.accessUserID = accessUserID;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getIdstr() {
        return idstr;
    }
    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }
    public String getScreen_name() {
        return screen_name;
    }
    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getProvince() {
        return province;
    }
    public void setProvince(int province) {
        this.province = province;
    }
    public int getCity() {
        return city;
    }
    public void setCity(int city) {
        this.city = city;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getProfile_image_url() {
        return profile_image_url;
    }
    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }
    public String getProfile_url() {
        return profile_url;
    }
    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getWeihao() {
        return weihao;
    }
    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public int getFollowers_count() {
        return followers_count;
    }
    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }
    public int getFriends_count() {
        return friends_count;
    }
    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }
    public int getStatuses_count() {
        return statuses_count;
    }
    public void setStatuses_count(int statuses_count) {
        this.statuses_count = statuses_count;
    }
    public int getFavourites_count() {
        return favourites_count;
    }
    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public Boolean getFollowing() {
        return following;
    }
    public void setFollowing(Boolean following) {
        this.following = following;
    }
    public Boolean getAllow_all_act_msg() {
        return allow_all_act_msg;
    }
    public void setAllow_all_act_msg(Boolean allow_all_act_msg) {
        this.allow_all_act_msg = allow_all_act_msg;
    }
    public Boolean getGeo_enabled() {
        return geo_enabled;
    }
    public void setGeo_enabled(Boolean geo_enabled) {
        this.geo_enabled = geo_enabled;
    }
    public Boolean getVerified() {
        return verified;
    }
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    public int getVerified_type() {
        return verified_type;
    }
    public void setVerified_type(int verified_type) {
        this.verified_type = verified_type;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Boolean getAllow_all_comment() {
        return allow_all_comment;
    }
    public void setAllow_all_comment(Boolean allow_all_comment) {
        this.allow_all_comment = allow_all_comment;
    }
    public String getAvatar_large() {
        return avatar_large;
    }
    public void setAvatar_large(String avatar_large) {
        this.avatar_large = avatar_large;
    }
    public String getAvatar_hd() {
        return avatar_hd;
    }
    public void setAvatar_hd(String avatar_hd) {
        this.avatar_hd = avatar_hd;
    }
    public String getVerified_reason() {
        return verified_reason;
    }
    public void setVerified_reason(String verified_reason) {
        this.verified_reason = verified_reason;
    }
    public Boolean getFollow_me() {
        return follow_me;
    }
    public void setFollow_me(Boolean follow_me) {
        this.follow_me = follow_me;
    }
    public int getOnline_status() {
        return online_status;
    }
    public void setOnline_status(int online_status) {
        this.online_status = online_status;
    }
    public int getBi_followers_count() {
        return bi_followers_count;
    }
    public void setBi_followers_count(int bi_followers_count) {
        this.bi_followers_count = bi_followers_count;
    }
    public String getLang() {
        return lang;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }
}
