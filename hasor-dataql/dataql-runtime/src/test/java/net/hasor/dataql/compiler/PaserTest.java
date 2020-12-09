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
import net.hasor.dataql.Finder;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.CompilerArguments.CodeLocationEnum;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
    public void testPaser_2() {
        try {
            QueryHelper.queryParser("return a == b ? c : d");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("no viable alternative at input");
        }
    }
}