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
package net.demo.hasor.domain.futures;
/**
 * 各种联系渠道
 * @version : 2016年08月08日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserContactInfo {
    private String             mobilePhone   = null; //电话
    private String             tencentNumber = null; //QQ号码
    private ContactAddressInfo userAddress   = null; //
    private ContactAddressInfo homeAddress   = null; //
    private String             blogHome      = null; //
    //
    public String getMobilePhone() {
        return mobilePhone;
    }
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
    public String getTencentNumber() {
        return tencentNumber;
    }
    public void setTencentNumber(String tencentNumber) {
        this.tencentNumber = tencentNumber;
    }
    public ContactAddressInfo getUserAddress() {
        return userAddress;
    }
    public void setUserAddress(ContactAddressInfo userAddress) {
        this.userAddress = userAddress;
    }
    public ContactAddressInfo getHomeAddress() {
        return homeAddress;
    }
    public void setHomeAddress(ContactAddressInfo homeAddress) {
        this.homeAddress = homeAddress;
    }
    public String getBlogHome() {
        return blogHome;
    }
    public void setBlogHome(String blogHome) {
        this.blogHome = blogHome;
    }
}