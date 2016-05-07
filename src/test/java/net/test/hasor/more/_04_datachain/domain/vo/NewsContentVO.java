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
package net.test.hasor.more._04_datachain.domain.vo;
import java.util.Date;
import java.util.List;
/**
 * 新闻帖子，数据库表
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class NewsContentVO {
    private long               id;
    private String             title;
    private String             author;
    private List<CloumnVO>     cloumnList;
    private List<NewsImagesVO> imageList;
    private List<String>       tagList;
    private String             jsonContent;
    private Date               createTime;
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
    public List<CloumnVO> getCloumnList() {
        return cloumnList;
    }
    public void setCloumnList(List<CloumnVO> cloumnList) {
        this.cloumnList = cloumnList;
    }
    public List<NewsImagesVO> getImageList() {
        return imageList;
    }
    public void setImageList(List<NewsImagesVO> imageList) {
        this.imageList = imageList;
    }
    public List<String> getTagList() {
        return tagList;
    }
    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
    public String getJsonContent() {
        return jsonContent;
    }
    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}