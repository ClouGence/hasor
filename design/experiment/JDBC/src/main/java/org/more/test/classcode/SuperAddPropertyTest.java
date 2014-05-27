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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.more.classcode.ClassEngine;
import org.more.classcode.InvokeException;
import org.more.classcode.MethodDelegate;
import org.more.classcode.PropertyDelegate;
import org.more.classcode.PropertyException;
/**
 *
 * @version 2010-8-25
 * @author 赵永春 (zyc@hasor.net)
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SuperAddPropertyTest {
    @Test
    public void addPropertyBase() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.addProperty("name", String.class);
        Object obj = ce.newInstance(null);
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
        ce.addProperty("name", new Super_NamePropertyDelegate());
        Object obj = ce.newInstance(null);
        //
        Method wm = obj.getClass().getMethod("setName", new Class[] { String.class });
        Method rm = obj.getClass().getMethod("getName");
        //
        System.out.println(rm.invoke(obj));
        wm.invoke(obj, new Object[] { "affa" });
        System.out.println(rm.invoke(obj));
    };
    @Test
    public void addInterface() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.addDelegate(Map.class, new Super_InterfaceDelegate());
        Map obj = (Map) ce.newInstance(null);
        System.out.println(obj.get("name"));
        obj.put("name", "fuck");
        System.out.println(obj.get("name"));
    }
}
class Super_NamePropertyDelegate implements PropertyDelegate<String> {
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
class Super_InterfaceDelegate implements MethodDelegate {
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