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
package net.demo.hasor.domain.access;
import net.demo.hasor.domain.AccessInfo;
import net.demo.hasor.manager.oauth.TencentOAuth;
/**
 * 腾讯 Token 信息
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class TencentAccessInfo extends AccessInfo {
    private String  accessToken      = null;
    private long    expiresTime      = 0;
    private String  openID           = null;
    private String  oriInfo          = null;
    //
    private String  gender           = null;   //获取用户性别 - 男 女
    private int     level            = 0;      //获取用户的黄钻等级
    private String  nickName         = null;   //获取用户昵称
    private boolean vip              = false;  //获取用户的vip信息
    private boolean yellowYearVip    = false;  //获取用户是否为年费黄钻用户信息
    private String  avatarURL30      = "";
    private String  avatarURL50      = "";
    private String  avatarURL100     = "";
    //
    private String  cityCode         = null;   //用户所在的城市代码
    private String  countryCode      = null;   //用户所在的国家代码
    private String  provinceCode     = null;   //用户所在的省份代码
    private String  homeCityCode     = null;   //用户的家乡所在的城市代码
    private String  homeCountryCode  = null;   //用户的家乡所在的国家代码
    private String  homeProvinceCode = null;   //用户的家乡所在的省份代码
    private String  homeTownCode     = null;   //用户的家乡所在的城镇代码
    private String  email            = null;   //用户邮箱
    private String  birthday         = null;   //用户的生日信息
    private int     weiboLevel       = 0;      //用户的微博等级
    private String  weiboName        = null;   //微博账号名
    //
    //
    public TencentAccessInfo() {
        this.setProvider(TencentOAuth.PROVIDER_NAME);
    }
    @Override
    public String getExternalUserID() {
        return this.getOpenID();
    }
    //
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public long getExpiresTime() {
        return expiresTime;
    }
    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }
    public String getOpenID() {
        return openID;
    }
    public void setOpenID(String openID) {
        this.openID = openID;
    }
    public String getOriInfo() {
        return oriInfo;
    }
    public void setOriInfo(String oriInfo) {
        this.oriInfo = oriInfo;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public boolean isVip() {
        return vip;
    }
    public void setVip(boolean vip) {
        this.vip = vip;
    }
    public boolean isYellowYearVip() {
        return yellowYearVip;
    }
    public void setYellowYearVip(boolean yellowYearVip) {
        this.yellowYearVip = yellowYearVip;
    }
    public String getAvatarURL30() {
        return avatarURL30;
    }
    public void setAvatarURL30(String avatarURL30) {
        this.avatarURL30 = avatarURL30;
    }
    public String getAvatarURL50() {
        return avatarURL50;
    }
    public void setAvatarURL50(String avatarURL50) {
        this.avatarURL50 = avatarURL50;
    }
    public String getAvatarURL100() {
        return avatarURL100;
    }
    public void setAvatarURL100(String avatarURL100) {
        this.avatarURL100 = avatarURL100;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getCityCode() {
        return cityCode;
    }
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getWeiboLevel() {
        return weiboLevel;
    }
    public void setWeiboLevel(int weiboLevel) {
        this.weiboLevel = weiboLevel;
    }
    public String getWeiboName() {
        return weiboName;
    }
    public void setWeiboName(String weiboName) {
        this.weiboName = weiboName;
    }
    public String getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
    public String getHomeCityCode() {
        return homeCityCode;
    }
    public void setHomeCityCode(String homeCityCode) {
        this.homeCityCode = homeCityCode;
    }
    public String getHomeCountryCode() {
        return homeCountryCode;
    }
    public void setHomeCountryCode(String homeCountryCode) {
        this.homeCountryCode = homeCountryCode;
    }
    public String getHomeProvinceCode() {
        return homeProvinceCode;
    }
    public void setHomeProvinceCode(String homeProvinceCode) {
        this.homeProvinceCode = homeProvinceCode;
    }
    public String getHomeTownCode() {
        return homeTownCode;
    }
    public void setHomeTownCode(String homeTownCode) {
        this.homeTownCode = homeTownCode;
    }
}
