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
package net.test.hasor.more._04_datachain.datafilter;
import net.hasor.core.Inject;
import net.test.hasor.more._04_datachain.dao.CloumnDao;
import net.test.hasor.more._04_datachain.domain.dto.NewsContentDO;
import net.test.hasor.more._04_datachain.domain.vo.CloumnVO;
import net.test.hasor.more._04_datachain.domain.vo.NewsContentVO;
import org.more.bizcommon.datachain.DataFilter;
import org.more.bizcommon.datachain.DataFilterChain;
import org.more.bizcommon.datachain.Domain;
import org.more.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 负责例子中，新闻所属栏目转换。
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class CloumnFilter implements DataFilter<NewsContentDO, NewsContentVO> {
    @Inject
    private CloumnDao cloumnDao;
    @Override
    public NewsContentVO doForward(Domain<NewsContentDO> domain, DataFilterChain<NewsContentDO, NewsContentVO> chain) throws Throwable {
        NewsContentDO dto = domain.getDomain();
        NewsContentVO vo = chain.doForward(domain);
        //
        String cloumnStr = dto.getCloumnIds();
        String[] cloumnArrays = cloumnStr.split(",");
        List<CloumnVO> cloumnList = new ArrayList<CloumnVO>();
        for (String cloumnIdStr : cloumnArrays) {
            if (StringUtils.isBlank(cloumnIdStr))
                continue;
            //
            long cloumnId = Long.parseLong(cloumnIdStr);
            CloumnVO cloumnVo = this.cloumnDao.getCloumnById(cloumnId);
            cloumnList.add(cloumnVo);
        }
        //
        vo.setCloumnList(cloumnList);
        return vo;
    }
    @Override
    public NewsContentDO doBackward(Domain<NewsContentVO> domain, DataFilterChain<NewsContentDO, NewsContentVO> chain) throws Throwable {
        NewsContentVO vo = domain.getDomain();
        NewsContentDO dto = chain.doBackward(domain);
        //
        String cloumnIds = "";
        List<CloumnVO> cloumnList = vo.getCloumnList();
        for (int i = 0; i < cloumnList.size(); i++) {
            CloumnVO cloumnVo = cloumnList.get(i);
            if (i == 0) {
                cloumnIds += cloumnVo.getId();
            } else {
                cloumnIds += ("," + cloumnVo.getId());
            }
        }
        //
        dto.setCloumnIds(cloumnIds);
        return dto;
    }
}