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
package net.test.hasor.core._07_datachain.domain.dto;
/**
 * 新闻帖子中的图片
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class NewsImagesDO {
    private long   id;
    private long   newsId;
    private String imgUrl;
    private String desc;
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getNewsId() {
        return newsId;
    }
    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}