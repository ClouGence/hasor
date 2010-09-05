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
package org.test.more.classcode;
import java.util.HashMap;
import org.junit.Test;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassEngine;
/**
 *
 * @version 2010-8-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassTest {
    @Test
    public void test_1() throws Exception {
        ClassEngine ce = new ClassEngine();
        System.out.println(ce.newInstance(null));
    };
    @Test
    public void test_2() throws Exception {
        ClassEngine ce = new ClassEngine(String.class);
        System.out.println(ce.newInstance(null));//TODO 跑出错误是正确的结果。
    }
    @Test
    public void test_3() throws Exception {
        ClassEngine ce = new ClassEngine(ClassTest.class);
        System.out.println(ce.newInstance(null));
    }
    @Test
    public void test_4() throws Exception {
        ClassEngine ce = new ClassEngine("aaa");
        System.out.println(ce.newInstance(null));
    }
    //----------------------------------------------------------
    @Test
    public void test_5() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        System.out.println(ce.newInstance(new Object()));
    };
    @Test
    public void test_6() throws Exception {
        ClassEngine ce = new ClassEngine(String.class);
        ce.setBuilderMode(BuilderMode.Propxy);
        System.out.println(ce.newInstance(""));//TODO 跑出错误是正确的结果。
    }
    @Test
    public void test_7() throws Exception {
        ClassEngine ce = new ClassEngine(ClassTest.class);
        ce.setBuilderMode(BuilderMode.Propxy);
        System.out.println(ce.newInstance(new ClassTest()));
    }
    @Test
    public void test_8() throws Exception {
        ClassEngine ce = new ClassEngine("aaa");
        ce.setBuilderMode(BuilderMode.Propxy);
        System.out.println(ce.newInstance(new HashMap<Object, Object>()));
    }
}