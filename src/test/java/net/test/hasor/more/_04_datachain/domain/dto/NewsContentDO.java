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
package net.test.hasor.more._04_datachain.domain.dto;
import java.util.Date;
import java.util.List;
/**
 * 新闻帖子，数据库表
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class NewsContentDO {
    private long               id;
    private String             title;
    private String             author;
    private String             cloumnIds;
    private String             jsonContent;
    private String             tags;
    private Date               createTime;
    //
    private List<NewsImagesDO> images;     //不做数据库映射
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCloumnIds() {
        return cloumnIds;
    }
    public void setCloumnIds(String cloumnIds) {
        this.cloumnIds = cloumnIds;
    }
    public String getJsonContent() {
        return jsonContent;
    }
    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public List<NewsImagesDO> getImages() {
        return images;
    }
    public void setImages(List<NewsImagesDO> images) {
        this.images = images;
    }
}