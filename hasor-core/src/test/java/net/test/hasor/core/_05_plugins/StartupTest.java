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
package net.test.hasor.core._05_plugins;
import com.alibaba.fastjson.JSON;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.test.hasor.core._01_bean.pojo.PojoBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * 1.启动模块
 * @version : 2015年11月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartupTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    @Test
    public void startupTest() {
        System.out.println("--->>startupTest<<--");
        AppContext appContext = Hasor.createAppContext("startup-config.xml");
        logger.debug("---------------------------------------------");
        //
        PojoBean myBean = appContext.getInstance(PojoBean.class);
        List<String> says = appContext.findBindingBean(String.class);
        logger.debug(JSON.toJSONString(myBean));
        assert says.size() > 0;
    }
}