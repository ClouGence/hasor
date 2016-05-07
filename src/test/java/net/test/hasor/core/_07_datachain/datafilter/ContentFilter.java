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
package net.test.hasor.core._07_datachain.datafilter;
import java.util.Arrays;
import org.more.util.StringUtils;
import net.hasor.core.Inject;
import net.hasor.plugins.datachain.DataFilter;
import net.hasor.plugins.datachain.DataFilterChain;
import net.hasor.plugins.datachain.Domain;
import net.test.hasor.core._07_datachain.dao.CloumnDao;
import net.test.hasor.core._07_datachain.domain.dto.NewsContentDO;
import net.test.hasor.core._07_datachain.domain.vo.NewsContentVO;
/**
 * 内容本身转换
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ContentFilter implements DataFilter<NewsContentDO, NewsContentVO> {
    @Inject
    private CloumnDao cloumnDao;
    @Override
    public NewsContentVO doForward(Domain<NewsContentDO> domain, DataFilterChain<NewsContentDO, NewsContentVO> chain) throws Throwable {
        NewsContentDO dto = domain.getDomain();
        NewsContentVO vo = chain.doForward(domain);
        //
        vo.setAuthor(dto.getAuthor());
        vo.setCreateTime(dto.getCreateTime());
        vo.setId(dto.getId());
        vo.setJsonContent(dto.getJsonContent());
        vo.setTitle(dto.getTitle());
        String tags = dto.getTags();
        if (StringUtils.isBlank(tags) == false) {
            vo.setTagList(Arrays.asList(tags.split(" ")));
        }
        //
        return vo;
    }
    @Override
    public NewsContentDO doBackward(Domain<NewsContentVO> domain, DataFilterChain<NewsContentDO, NewsContentVO> chain) throws Throwable {
        NewsContentVO vo = domain.getDomain();
        NewsContentDO dto = chain.doBackward(domain);
        //
        dto.setAuthor(vo.getAuthor());
        dto.setCreateTime(vo.getCreateTime());
        dto.setId(vo.getId());
        dto.setJsonContent(vo.getJsonContent());
        dto.setTitle(vo.getTitle());
        String tags = "";
        if (vo.getTagList() != null) {
            for (String tag : vo.getTagList()) {
                if (StringUtils.isBlank(tag) == false) {
                    tags += (tag + " ");
                }
            }
        }
        dto.setTags(tags);
        //
        return dto;
    }
}