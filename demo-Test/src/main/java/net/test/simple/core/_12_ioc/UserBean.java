/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.simple.core._12_ioc;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
/**
 * 
 * @version : 2014年9月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserBean implements InjectMembers {
    private String       userID   = "1234";
    private String       userName = "测试用户";
    private UserTypeBean userType = null;
    //
    public void doInject(AppContext appContext) {
        this.userType = appContext.getInstance(UserTypeBean.class);
    }
    //
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public UserTypeBean getUserType() {
        return userType;
    }
    public void setUserType(UserTypeBean userType) {
        this.userType = userType;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}