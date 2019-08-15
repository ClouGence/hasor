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
import net.hasor.core.BindInfo;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.test.actions.AnnoPostGetAction;
import net.hasor.test.actions.async.ClassAsyncAction;
import net.hasor.test.actions.async.GetAsyncAction;
import net.hasor.test.actions.async.MethodAsyncAction;
import net.hasor.test.actions.basic.AnnoDeleteAction;
import net.hasor.test.actions.basic.AnnoGetAction;
import net.hasor.test.actions.basic.BasicAction;
import net.hasor.web.AbstractTest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Predicate;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefMappingTest extends AbstractTest {
    @Test
    public void defTest_1() {
        String mappingTo = "/abc/abc.*";
        BindInfo<?> targetType = bindInfo(BasicAction.class);
        //
        MappingDef def1 = new MappingDef(1243, targetType, mappingTo, Matchers.anyMethod());
        assert def1.getIndex() == 1243;
        assert def1.getHttpMapping().size() == 0;
        assert def1.getMappingTo().equals(mappingTo);
        assert "/abc/abc.do".matches(def1.getMappingToMatches());
        assert !"/abc/abe.do".matches(def1.getMappingToMatches());
        assert def1.getTargetType() == targetType;
        assert def1.toString().startsWith("pattern=");
        //
        try {
            new MappingDef(1243, targetType, "", Matchers.anyMethod());
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith("Service path is empty.");
        }
        //
        try {
            new MappingDef(1243, targetType, "bcd.htm", Matchers.anyMethod());
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith("Service path format error, must be a '/' at the start.");
        }
    }

    @Test
    public void defTest_2() {
        String mappingTo = "/abc/abc.*";
        Predicate<Method> methodMatcher2 = method -> method.getName().equals("execute");
        //
        MappingDef def2 = new MappingDef(1243, bindInfo(BasicAction.class), mappingTo, methodMatcher2);
        assert def2.getHttpMapping().size() == 0;
        assert def2.getHttpMethodSet().length == 0;
        //
        MappingDef def3 = new MappingDef(1243, bindInfo(BasicAction.class), mappingTo, methodMatcher2, false);
        assert def3.getHttpMapping().size() == 1;
        assert def3.getHttpMapping().get("ANY") != null;
        assert def3.getHttpMethodSet().length == 1;
        //
        MappingDef def4 = new MappingDef(1243, bindInfo(AnnoPostGetAction.class), mappingTo, Matchers.anyMethod());
        assert def4.getHttpMapping().size() == 2;
        assert def4.getHttpMapping().get("GET").getName().equals("doGet");
        assert def4.getHttpMapping().get("POST").getName().equals("doPost");
        assert def4.getHttpMethodSet().length == 2;
        //
        MappingDef def5 = new MappingDef(1243, bindInfo(AnnoDeleteAction.class), mappingTo, Matchers.anyMethod());
        assert def5.getHttpMapping().get("DELETE").getName().equals("doDelete");
        assert def5.getHttpMethodSet().length == 1;
        //
        MappingDef def6 = new MappingDef(1243, bindInfo(ClassAsyncAction.class), mappingTo, Matchers.anyMethod());
        assert def6.getHttpMapping().get("ANY").getName().equals("execute");
        assert def6.getHttpMethodSet().length == 1;
        //
        MappingDef def7 = new MappingDef(1243, bindInfo(MethodAsyncAction.class), mappingTo, Matchers.anyMethod());
        assert def7.getHttpMapping().get("ANY").getName().equals("execute");
        assert def7.getHttpMethodSet().length == 1;
    }

    @Test
    public void defTest_3() throws MalformedURLException {
        String mappingTo = "/abc/abc.*";
        //
        MappingDef def1 = new MappingDef(1243, bindInfo(MethodAsyncAction.class), mappingTo, Matchers.anyMethod());
        HttpServletRequest request1 = mockRequest("post", new URL("http://www.hasor.net/special_param.do"));
        assert !def1.matchingMapping(request1);
        assert def1.isAsync(request1);
        //
        MappingDef def2 = new MappingDef(1243, bindInfo(MethodAsyncAction.class), mappingTo, Matchers.anyMethod());
        HttpServletRequest request2 = mockRequest("post", new URL("http://www.hasor.net/abc.html"));
        assert !def2.matchingMapping(request2);
        assert def2.isAsync(request2);
        //
        MappingDef def3 = new MappingDef(1243, bindInfo(AnnoGetAction.class), mappingTo, Matchers.anyMethod());
        HttpServletRequest request3 = mockRequest("post", new URL("http://www.hasor.net/abc/abc.html"));
        assert !def3.matchingMapping(request3);
        assert !def3.isAsync(request3);
        //
        MappingDef def4 = new MappingDef(1243, bindInfo(GetAsyncAction.class), mappingTo, Matchers.anyMethod());
        HttpServletRequest request4 = mockRequest("post", new URL("http://www.hasor.net/abc/abc.html"));
        assert !def4.matchingMapping(request4);
        assert !def4.isAsync(request4); // 先找到目标执行方法，然后在确认是否可以异步执行。因为招不到目标方法，所以异步执行的判断始终为 false
        //
        MappingDef def5 = new MappingDef(1243, bindInfo(MethodAsyncAction.class), mappingTo, Matchers.anyMethod());
        HttpServletRequest request5 = mockRequest("post", new URL("http://www.hasor.net/abc/abc.html"));
        assert def5.matchingMapping(request5);
        assert def5.isAsync(request5);
        //
        MappingDef def6 = new MappingDef(1243, bindInfo(GetAsyncAction.class), mappingTo, Matchers.anyMethod());
        HttpServletRequest request6 = mockRequest("get", new URL("http://www.hasor.net/abc/abc.html"));
        assert def6.matchingMapping(request6);
        assert def6.isAsync(request6);
    }

    @Test
    public void defTest_4() throws Throwable {
        HttpServletRequest request1 = mockRequest("POST", new URL("http://www.hasor.net/abc/abc.html"));
        HttpServletRequest request2 = mockRequest("get", new URL("http://www.hasor.net/abc/abc.html"));
        HttpServletRequest request3 = mockRequest("ADD", new URL("http://www.hasor.net/abc/abc.html"));
        HttpServletRequest request4 = mockRequest("DELETE", new URL("http://www.hasor.net/abc/abc.html"));
        HttpServletRequest request5 = mockRequest("OPTION", new URL("http://www.hasor.net/abc/abc.html"));
        //
        MappingDef def = new MappingDef(1243, bindInfo(AnnoPostGetAction.class), "/execute.do", Matchers.anyMethod());
        assert def.findMethod(request1).getName().equals("doPost");
        assert def.findMethod(request2).getName().equals("doGet");
        assert def.findMethod(request3) == null;
        assert def.findMethod(request4) == null;
        assert def.findMethod(request5) == null;
    }
}