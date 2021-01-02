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
package net.hasor.dataql.compiler;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.runtime.QueryHelper;
import org.junit.Test;

import java.io.IOException;

/**
 * 测试用例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public class PaserTest extends AbstractTestResource {
    @Test
    public void testPaser_1() {
        try {
            QueryHelper.queryParser("return ${a} -1");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("no viable alternative at input");
        }
    }

    @Test
    public void testPaser_2() throws IOException {
        QueryHelper.queryParser("return a == b ? c : d");
        assert true;
    }

    @Test
    public void testPaser_3() throws IOException {
        QueryHelper.queryParser("return 123,a == b ? c : d");
        assert true;
    }
}