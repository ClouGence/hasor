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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.more.InvokeException;
import org.more.PropertyException;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.MethodDelegate;
import org.more.core.classcode.PropertyDelegate;
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class PropxyAddPropertyTest {
    @Test
    public void addPropertyBase() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addProperty("name", String.class);
        Object obj = ce.newInstance(new Object());
        //
        Method wm = obj.getClass().getMethod("setName", new Class[] { String.class });
        Method rm = obj.getClass().getMethod("getName");
        //
        System.out.println(rm.invoke(obj));
        wm.invoke(obj, new Object[] { "affa" });
        System.out.println(rm.invoke(obj));
    };
    @Test
    public void addPropertyDelegate() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addProperty("name", new Propxy_NamePropertyDelegate());
        Object obj = ce.newInstance(new Object());
        //
        Method wm = obj.getClass().getMethod("setName", new Class[] { String.class });
        Method rm = obj.getClass().getMethod("getName");
        //
        System.out.println(rm.invoke(obj));
        wm.invoke(obj, new Object[] { "affa" });
        System.out.println(rm.invoke(obj));
    };
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInterface() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addDelegate(Map.class, new Propxy_InterfaceDelegate());
        Map obj = (Map) ce.newInstance(new Object());
        System.out.println(obj.get("name"));
        obj.put("name", "fuck");
        System.out.println(obj.get("name"));
    }
}
class Propxy_NamePropertyDelegate implements PropertyDelegate<String> {
    private String value = null;
    public Class<? extends String> getType() {
        return String.class;
    }
    public String get(Object target) throws PropertyException {
        return this.value;
    }
    public void set(Object target, String newValue) throws PropertyException {
        this.value = newValue;
    }
}
class Propxy_InterfaceDelegate implements MethodDelegate {
    private HashMap<Object, Object> value = new HashMap<Object, Object>();
    public Object invoke(Method callMethod, Object target, Object[] params) throws InvokeException {
        if (callMethod.getName().equals("get"))
            return this.value.get(params[0]);
        else if (callMethod.getName().equals("put"))
            this.value.put(params[0], params[1]);
        else
            System.out.println(callMethod);
        return null;
    }
}