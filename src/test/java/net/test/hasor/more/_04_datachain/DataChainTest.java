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
package net.test.hasor.more._04_datachain;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.test.hasor.more._04_datachain.datafilter.CloumnFilter;
import net.test.hasor.more._04_datachain.datafilter.ContentFilter;
import net.test.hasor.more._04_datachain.datafilter.ImagesFilter;
import net.test.hasor.more._04_datachain.domain.dto.NewsContentDO;
import net.test.hasor.more._04_datachain.domain.vo.NewsContentVO;
import org.junit.Test;
import org.more.bizcommon.datachain.DataChainContext;

import java.util.Date;
/**
 * 数据对象转换工具，提供 A 类型对象到 B 类型对象转换功能。并使开发者在转换过程中可以实现更加高级别的控制协调能力。
 * 使用场景：
 *  如，DO 到 TO or VO，以及各种 O 之间的数据转换，这些数据对象随着业务和团队组成，无法简单的 Bean copy 去解决数据转换问题。
 *  另外，随着业务模型的复杂度增加，类型转换可能会遍布应用程序的各个角落，DataChain可以帮你归类整理类型转换。使其可以从用复用。
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class DataChainTest {
    @Test
    public void dataChainTest() throws Throwable {
        AppContext app = Hasor.createAppContext();
        DataChainContext<NewsContentDO, NewsContentVO> dataChain = new DataChainContext<NewsContentDO, NewsContentVO>() {
        };
        //
        dataChain.addDataFilter("body", app.getInstance(ContentFilter.class));
        dataChain.addDataFilter("image", app.getInstance(ImagesFilter.class));
        dataChain.addDataFilter("cloumn", app.getInstance(CloumnFilter.class));
        //
        NewsContentDO contextDO = new NewsContentDO();
        contextDO.setAuthor("author");
        contextDO.setCloumnIds("1,2,3,4");
        contextDO.setCreateTime(new Date());
        contextDO.setJsonContent("[{},{}]");
        contextDO.setTags("美女 直播 萌萌哒");
        contextDO.setTitle("娇贵女生变身主播.");
        //
        NewsContentVO vo = dataChain.doChain(contextDO);
        System.out.println(vo);
        //
    }
}