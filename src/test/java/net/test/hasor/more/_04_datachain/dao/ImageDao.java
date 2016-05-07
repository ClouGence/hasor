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
package net.test.hasor.more._04_datachain.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.test.hasor.more._04_datachain.domain.dto.NewsImagesDO;
/**
 * 
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ImageDao {
    public List<NewsImagesDO> queryImagesByNewsId(long newsId) {
        List<NewsImagesDO> list = new ArrayList<NewsImagesDO>();
        int maxImages = new Random().nextInt(10);
        for (int i = 0; i <= maxImages; i++) {
            NewsImagesDO imagesDO = new NewsImagesDO();
            imagesDO.setId(System.currentTimeMillis());
            imagesDO.setNewsId(newsId);
            imagesDO.setDesc("XXX-" + Long.toString(System.currentTimeMillis(), 16).toUpperCase());
            imagesDO.setImgUrl("http://www.google.cn/" + imagesDO.getDesc() + ".jpg");
            list.add(imagesDO);
        }
        return list;
    }
}