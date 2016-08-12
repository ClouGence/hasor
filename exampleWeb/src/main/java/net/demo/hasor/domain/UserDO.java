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
package net.demo.hasor.domain;
import net.demo.hasor.domain.enums.GenderType;
import net.demo.hasor.domain.enums.UserStatus;
import net.demo.hasor.domain.enums.UserType;
import net.demo.hasor.domain.futures.UserContactInfo;
import net.demo.hasor.domain.futures.UserFutures;

import java.util.Date;
import java.util.List;
/**
 * 用户数据
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDO {
    private long               userID         = 0;    // UserID（PK，自增）
    private String             account        = null; // 帐号（唯一）
    private String             email          = null; // email
    private String             mobilePhone    = null; // 移动电话
    private String             password       = null; // 密码(非明文)
    private UserType           type           = null; // 帐号类型
    private String             nick           = null; // 昵称
    private GenderType         gender         = null; // 用户性别(男女)
    private String             avatar         = null; // 头像
    private UserStatus         status         = null; // 状态
    private long               loginCount     = 0;    // 登录次数
    private Date               firstLoginTime = null; // 首次登陆时间
    private Date               lastLoginTime  = null; // 最后一次登陆时间
    private Date               createTime     = null; // 创建时间
    private Date               modifyTime     = null; // 修噶改时间
    //
    private List<UserSourceDO> userSourceList = null; //外部平台登陆信息
    private UserContactInfo    contactInfo    = null; //各种联系方式(json格式)
    private UserFutures        futures        = null; //扩展信息(json格式)
    //
    //
    public long getUserID() {
        return userID;
    }
    public void setUserID(long userID) {
        this.userID = userID;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobilePhone() {
        return mobilePhone;
    }
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }
    public GenderType getGender() {
        return gender;
    }
    public void setGender(GenderType gender) {
        this.gender = gender;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public List<UserSourceDO> getUserSourceList() {
        return userSourceList;
    }
    public void setUserSourceList(List<UserSourceDO> userSourceList) {
        this.userSourceList = userSourceList;
    }
    public UserContactInfo getContactInfo() {
        return contactInfo;
    }
    public void setContactInfo(UserContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
    public UserFutures getFutures() {
        return futures;
    }
    public void setFutures(UserFutures futures) {
        this.futures = futures;
    }
    public UserStatus getStatus() {
        return status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    public Date getFirstLoginTime() {
        return firstLoginTime;
    }
    public void setFirstLoginTime(Date firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }
    public Date getLastLoginTime() {
        return lastLoginTime;
    }
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    public long getLoginCount() {
        return loginCount;
    }
    public void setLoginCount(long loginCount) {
        this.loginCount = loginCount;
    }
    public UserType getType() {
        return type;
    }
    public void setType(UserType type) {
        this.type = type;
    }
}
