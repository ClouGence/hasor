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
package net.hasor.web.binder;
import net.hasor.web.AbstractTest;
import net.hasor.web.InvokerConfig;
import org.junit.Test;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import java.util.Map;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class ConfigTest extends AbstractTest {
    @Test
    public void configTest_1() {
        OneConfig oneConfig = new OneConfig();
        oneConfig.put("abc", "abc");
        //
        assert new OneConfig((FilterConfig) oneConfig, null).get("abc").equals("abc");
        assert new OneConfig((ServletConfig) oneConfig, null).get("abc").equals("abc");
        assert new OneConfig("", (InvokerConfig) oneConfig, null).get("abc").equals("abc");
        assert new OneConfig("", (Map) oneConfig, null).get("abc").equals("abc");
    }
}