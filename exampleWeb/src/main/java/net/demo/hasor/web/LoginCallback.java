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
package net.demo.hasor.web;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.PageFans;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.PageFansBean;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.javabeans.weibo.Company;
import com.qq.connect.oauth.Oauth;
import net.demo.hasor.domain.LoginCallBackForm;
import net.hasor.restful.WebController;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
import org.more.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 * QQ 登录认证
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/login_callback.do")
public class LoginCallback extends WebController {
    //
    public void execute(@Params LoginCallBackForm loginForm) throws IOException, QQConnectException {
        //
        //
        if (StringUtils.equalsIgnoreCase(loginForm.getProvider(), "qq")) {
            //
            this.getResponse().setContentType("text/html;charset=utf-8");
            this.getResponse().sendRedirect(new Oauth().getAuthorizeURL(this.getRequest()));
        } else {
            //
            getResponse().setContentType("text/html; charset=utf-8");
            PrintWriter out = getResponse().getWriter();
            //
            try {
                AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(getRequest());
                String accessToken = null, openID = null;
                long tokenExpireIn = 0L;
                if (accessTokenObj.getAccessToken().equals("")) {
                    //                我们的网站被CSRF攻击了或者用户取消了授权
                    //                做一些数据统计工作
                    System.out.print("没有获取到响应参数");
                } else {
                    accessToken = accessTokenObj.getAccessToken();
                    tokenExpireIn = accessTokenObj.getExpireIn();
                    setSessionAttr("demo_access_token", accessToken);
                    setSessionAttr("demo_token_expirein", String.valueOf(tokenExpireIn));
                    // 利用获取到的accessToken 去获取当前用的openid -------- start
                    OpenID openIDObj = new OpenID(accessToken);
                    openID = openIDObj.getUserOpenID();
                    out.println("欢迎你，代号为 " + openID + " 的用户!");
                    setSessionAttr("demo_openid", openID);
                    out.println("<a href=" + "/shuoshuoDemo.html" + " target=\"_blank\">去看看发表说说的demo吧</a>");
                    // 利用获取到的accessToken 去获取当前用户的openid --------- end
                    out.println("<p> start -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ---------------------------- start </p>");
                    UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                    UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                    out.println("<br/>");
                    if (userInfoBean.getRet() == 0) {
                        out.println(userInfoBean.getNickname() + "<br/>");
                        out.println(userInfoBean.getGender() + "<br/>");
                        out.println("黄钻等级： " + userInfoBean.getLevel() + "<br/>");
                        out.println("会员 : " + userInfoBean.isVip() + "<br/>");
                        out.println("黄钻会员： " + userInfoBean.isYellowYearVip() + "<br/>");
                        out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "/><br/>");
                        out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "/><br/>");
                        out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "/><br/>");
                    } else {
                        out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                    }
                    out.println("<p> end -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ---------------------------- end </p>");
                    out.println("<p> start ----------------------------------- 验证当前用户是否为认证空间的粉丝------------------------------------------------ start <p>");
                    PageFans pageFansObj = new PageFans(accessToken, openID);
                    PageFansBean pageFansBean = pageFansObj.checkPageFans("97700000");
                    if (pageFansBean.getRet() == 0) {
                        out.println("<p>验证您" + (pageFansBean.isFans() ? "是" : "不是") + "QQ空间97700000官方认证空间的粉丝</p>");
                    } else {
                        out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + pageFansBean.getMsg());
                    }
                    out.println("<p> end ----------------------------------- 验证当前用户是否为认证空间的粉丝------------------------------------------------ end <p>");
                    out.println("<p> start -----------------------------------利用获取到的accessToken,openid 去获取用户在微博的昵称等信息 ---------------------------- start </p>");
                    com.qq.connect.api.weibo.UserInfo weiboUserInfo = new com.qq.connect.api.weibo.UserInfo(accessToken, openID);
                    com.qq.connect.javabeans.weibo.UserInfoBean weiboUserInfoBean = weiboUserInfo.getUserInfo();
                    if (weiboUserInfoBean.getRet() == 0) {
                        //获取用户的微博头像----------------------start
                        out.println("<image src=" + weiboUserInfoBean.getAvatar().getAvatarURL30() + "/><br/>");
                        out.println("<image src=" + weiboUserInfoBean.getAvatar().getAvatarURL50() + "/><br/>");
                        out.println("<image src=" + weiboUserInfoBean.getAvatar().getAvatarURL100() + "/><br/>");
                        //获取用户的微博头像 ---------------------end
                        //获取用户的生日信息 --------------------start
                        out.println("<p>尊敬的用户，你的生日是： " + weiboUserInfoBean.getBirthday().getYear() + "年" + weiboUserInfoBean.getBirthday().getMonth() + "月" + weiboUserInfoBean.getBirthday().getDay() + "日");
                        //获取用户的生日信息 --------------------end
                        StringBuffer sb = new StringBuffer();
                        sb.append("<p>所在地:" + weiboUserInfoBean.getCountryCode() + "-" + weiboUserInfoBean.getProvinceCode() + "-" + weiboUserInfoBean.getCityCode() + weiboUserInfoBean.getLocation());
                        //获取用户的公司信息---------------------------start
                        ArrayList<Company> companies = weiboUserInfoBean.getCompanies();
                        if (companies.size() > 0) {
                            //有公司信息
                            for (int i = 0, j = companies.size(); i < j; i++) {
                                sb.append("<p>曾服役过的公司：公司ID-" + companies.get(i).getID() + " 名称-" + companies.get(i).getCompanyName() + " 部门名称-" + companies.get(i).getDepartmentName() + " 开始工作年-" + companies.get(i).getBeginYear() + " 结束工作年-" + companies.get(i).getEndYear());
                            }
                        } else {
                            //没有公司信息
                        }
                        //获取用户的公司信息---------------------------end
                        out.println(sb.toString());
                    } else {
                        out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + weiboUserInfoBean.getMsg());
                    }
                    out.println("<p> end -----------------------------------利用获取到的accessToken,openid 去获取用户在微博的昵称等信息 ---------------------------- end </p>");
                }
            } catch (QQConnectException e) {
            }
        }
    }
}