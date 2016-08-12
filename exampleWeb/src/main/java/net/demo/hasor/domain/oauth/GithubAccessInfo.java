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
import net.demo.hasor.manager.oauth.GithubOAuth;
import net.demo.hasor.manager.oauth.TencentOAuth;

import java.util.Map;
/**
 * Github Token 信息
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class GithubAccessInfo extends AccessInfo {
    private String              accessToken         = null;
    private String              id                  = null; //
    private String              login               = null; //
    private String              avatar_url          = null; //
    private String              gravatar_id         = null; //
    private String              url                 = null; //
    private String              html_url            = null; //
    private String              followers_url       = null; //
    private String              following_url       = null; //
    private String              gists_url           = null; //
    private String              starred_url         = null; //
    private String              subscriptions_url   = null; //
    private String              organizations_url   = null; //
    private String              repos_url           = null; //
    private String              events_url          = null; //
    private String              received_events_url = null; //
    private String              type                = null; //
    private String              site_admin          = null; //
    private String              name                = null; //
    private String              company             = null; //
    private String              blog                = null; //
    private String              location            = null; //
    private String              email               = null; //
    private String              hireable            = null; //
    private String              bio                 = null; //
    private String              public_repos        = null; //
    private String              public_gists        = null; //
    private String              followers           = null; //
    private String              following           = null; //
    private String              created_at          = null; //
    private String              updated_at          = null; //
    private String              private_gists       = null; //
    private String              total_private_repos = null; //
    private String              owned_private_repos = null; //
    private String              disk_usage          = null; //
    private String              collaborators       = null; //
    private Map<String, Object> plan                = null; //
    //
    //
    public GithubAccessInfo() {
        this.setProvider(GithubOAuth.PROVIDER_NAME);
    }
    @Override
    public String getExternalUserID() {
        return this.getId();
    }
    //
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getAvatar_url() {
        return avatar_url;
    }
    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
    public String getGravatar_id() {
        return gravatar_id;
    }
    public void setGravatar_id(String gravatar_id) {
        this.gravatar_id = gravatar_id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getHtml_url() {
        return html_url;
    }
    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }
    public String getFollowers_url() {
        return followers_url;
    }
    public void setFollowers_url(String followers_url) {
        this.followers_url = followers_url;
    }
    public String getFollowing_url() {
        return following_url;
    }
    public void setFollowing_url(String following_url) {
        this.following_url = following_url;
    }
    public String getGists_url() {
        return gists_url;
    }
    public void setGists_url(String gists_url) {
        this.gists_url = gists_url;
    }
    public String getStarred_url() {
        return starred_url;
    }
    public void setStarred_url(String starred_url) {
        this.starred_url = starred_url;
    }
    public String getSubscriptions_url() {
        return subscriptions_url;
    }
    public void setSubscriptions_url(String subscriptions_url) {
        this.subscriptions_url = subscriptions_url;
    }
    public String getOrganizations_url() {
        return organizations_url;
    }
    public void setOrganizations_url(String organizations_url) {
        this.organizations_url = organizations_url;
    }
    public String getRepos_url() {
        return repos_url;
    }
    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }
    public String getEvents_url() {
        return events_url;
    }
    public void setEvents_url(String events_url) {
        this.events_url = events_url;
    }
    public String getReceived_events_url() {
        return received_events_url;
    }
    public void setReceived_events_url(String received_events_url) {
        this.received_events_url = received_events_url;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSite_admin() {
        return site_admin;
    }
    public void setSite_admin(String site_admin) {
        this.site_admin = site_admin;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getBlog() {
        return blog;
    }
    public void setBlog(String blog) {
        this.blog = blog;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getHireable() {
        return hireable;
    }
    public void setHireable(String hireable) {
        this.hireable = hireable;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public String getPublic_repos() {
        return public_repos;
    }
    public void setPublic_repos(String public_repos) {
        this.public_repos = public_repos;
    }
    public String getPublic_gists() {
        return public_gists;
    }
    public void setPublic_gists(String public_gists) {
        this.public_gists = public_gists;
    }
    public String getFollowers() {
        return followers;
    }
    public void setFollowers(String followers) {
        this.followers = followers;
    }
    public String getFollowing() {
        return following;
    }
    public void setFollowing(String following) {
        this.following = following;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    public String getPrivate_gists() {
        return private_gists;
    }
    public void setPrivate_gists(String private_gists) {
        this.private_gists = private_gists;
    }
    public String getTotal_private_repos() {
        return total_private_repos;
    }
    public void setTotal_private_repos(String total_private_repos) {
        this.total_private_repos = total_private_repos;
    }
    public String getOwned_private_repos() {
        return owned_private_repos;
    }
    public void setOwned_private_repos(String owned_private_repos) {
        this.owned_private_repos = owned_private_repos;
    }
    public String getDisk_usage() {
        return disk_usage;
    }
    public void setDisk_usage(String disk_usage) {
        this.disk_usage = disk_usage;
    }
    public String getCollaborators() {
        return collaborators;
    }
    public void setCollaborators(String collaborators) {
        this.collaborators = collaborators;
    }
    public Map<String, Object> getPlan() {
        return plan;
    }
    public void setPlan(Map<String, Object> plan) {
        this.plan = plan;
    }
}