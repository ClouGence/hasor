/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.test.classcode;
import java.io.InputStream;
import java.util.HashMap;
import org.junit.Test;
import org.more.classcode.BuilderMode;
import org.more.classcode.ClassEngine;
import org.more.classcode.RootClassLoader;
/**
 *
 * @version 2010-8-25
 * @author 赵永春 (zyc@hasor.net)
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
        System.out.println(ce.newInstance(null));//跑出错误是正确的结果。原因是String为final。
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
        System.out.println(ce.newInstance(""));//跑出错误是正确的结果。原因是String为final。
    }
    @Test
    public void test_7() throws Exception {}
    @Test
    public void test_8() throws Exception {
        ClassEngine ce = new ClassEngine("aaa");
        ce.setBuilderMode(BuilderMode.Propxy);
        System.out.println(ce.newInstance(new HashMap<Object, Object>()));
    }
    @Test
    public void test_9() throws Exception {
        //生称一个类，然后获取这个生成类型的字节码。
        ClassEngine ce = new ClassEngine("org.aaa.Test", ClassTest.class, null);
        Class<?> type = ce.builderClass().toClass();
        InputStream is1 = type.getClassLoader().getResourceAsStream(type.getName().replace(".", "/") + ".class");
        InputStream is2 = type.getClassLoader().getResourceAsStream(type.getName());
        System.out.println("is1:\t" + is1);
        System.out.println("is2:\t" + is2);
    }
    @Test
    public void test_10() throws Exception {
        /*代理的代理*/
        RootClassLoader root = new RootClassLoader(ClassLoader.getSystemClassLoader());
        ClassEngine ce_1 = new ClassEngine(ClassTest.class, root);
        ce_1.setBuilderMode(BuilderMode.Propxy);
        Object obj_1 = ce_1.newInstance(new ClassTest());
        //
        ClassEngine ce_2 = new ClassEngine(obj_1.getClass(), root);
        ce_2.setBuilderMode(BuilderMode.Propxy);
        Object obj_2 = ce_2.newInstance(obj_1);
        System.out.println(obj_2);
    }
}