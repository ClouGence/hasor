/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.more.classcode.ClassEngine;
import org.more.classcode.ClassNameStrategy;
import org.more.classcode.DelegateStrategy;
import org.more.classcode.MethodStrategy;
import org.more.classcode.PropertyStrategy;
import org.more.classcode.objects.DefaultMethodDelegate;
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class StrategyTest {
    private ClassEngine getClassEngine() throws ClassNotFoundException {
        ClassEngine ce = new ClassEngine();
        ce.setClassNameStrategy(new Test_ClassNameStrategy());
        ce.setClassName(null);
        ce.setPropertyStrategy(new Test_PropertyStrategy());
        ce.setDelegateStrategy(new Test_DelegateStrategy());
        ce.setMethodStrategy(new Test_MethodStrategy());
        return ce;
    }
    @Test
    public void test_ClassNameStrategy() throws Exception {
        ClassEngine ce = getClassEngine();
        System.out.println(ce.newInstance(null));
        System.out.println(ce.newInstance(null));
        System.out.println(ce.newInstance(null));
    };
    @Test
    public void test_PropertyStrategy() throws Exception {
        ClassEngine ce = getClassEngine();
        ce.addProperty("message", String.class);
        ce.addProperty("error", Exception.class);
        ce.addProperty("classEngine", ClassEngine.class);
        ce.addProperty("intValue", int.class);
        ce.addProperty("byteValue", Byte.class);
        //
        System.out.println(ce.newInstance(null));
    };
    @Test
    public void test_DelegateStrategy() throws Exception {
        ClassEngine ce = getClassEngine();
        ce.addDelegate(List.class, new DefaultMethodDelegate());
        ce.addDelegate(Map.class, new DefaultMethodDelegate());
        ce.addDelegate(Serializable.class, new DefaultMethodDelegate());
        //
        Object obj = ce.newInstance(null);
        System.out.println(obj instanceof List);
        System.out.println(obj instanceof Map);
        System.out.println(obj instanceof Serializable);
    }
    @Test
    public void test_MethodStrategy() throws Exception {
        ClassEngine ce = getClassEngine();
        ce.setSuperClass(StrategyTest.class);
        ce.addDelegate(List.class, new DefaultMethodDelegate());
        ce.addDelegate(Map.class, new DefaultMethodDelegate());
        ce.addDelegate(Serializable.class, new DefaultMethodDelegate());
        //
        System.out.println(ce.newInstance(null));
    }
}
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class Test_ClassNameStrategy implements ClassNameStrategy {
    private String simpleNS = "org.mypackage.test_";
    public String generateName(Class<?> superClass) {
        return simpleNS + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    }
    public void initStrategy(ClassEngine classEngine) {}
}
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class Test_PropertyStrategy implements PropertyStrategy {
    public boolean isReadOnly(String name, Class<?> type, boolean isDelegate) {
        return true;//À˘”– Ù–‘æ˘÷ª∂¡
    }
    public boolean isWriteOnly(String name, Class<?> type, boolean isDelegate) {
        return false;
    }
    public boolean isIgnore(String name, Class<?> type, boolean isDelegate) {
        return name.equals("intValue");//∫ˆ¬‘intValue Ù–‘
    }
    public void initStrategy(ClassEngine classEngine) {}
}
class Test_DelegateStrategy implements DelegateStrategy {
    public boolean isIgnore(Class<?> delegateType) {
        return Map.class == delegateType;//∫ˆ¬‘MapΩ”ø⁄ µœ÷°£
    }
    public void initStrategy(ClassEngine classEngine) {}
}
class Test_MethodStrategy implements MethodStrategy {
    public boolean isIgnore(Class<?> superClass, Object ignoreMethod, boolean isConstructor) {
        System.out.println(ignoreMethod.toString());
        return false;
    }
    public void initStrategy(ClassEngine classEngine) {}
}