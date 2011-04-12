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
package org.more.hypha.el;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.hypha.ELContext;
import org.more.hypha.ELMethod;
import org.more.hypha.ELObject;
import org.more.workflow.el.PropertyBinding;
/**
 * 
 * Date : 2011-4-8
 * @author 赵永春
 */
public abstract class AbstractELContext extends OgnlContext implements ELContext {
    private static final long serialVersionUID = 3969623095960649450L;
    /***/
    protected abstract List<InputStream> getConfigStreams() throws IOException;
    /**解析配置文件，并且装载其中所定义的对象类型。*/
    public void loadConfig() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        List<InputStream> ins = this.getConfigStreams();
        Properties prop = new Properties();
        for (InputStream is : ins)
            prop.load(is);
        for (Object key : prop.keySet()) {
            String k = (String) key;
            String beanBuilderClass = prop.getProperty(k);
            Object builder = Class.forName(beanBuilderClass).getConstructor().newInstance();
            this.addEL(k, builder);
        }
    };
    public EvalExpression evalExpression(String expressionString) throws OgnlException {
        return new EvalExpression(this, expressionString);
    };
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws OgnlException {
        return new PropertyBinding(propertyEL, object);
    };
    public void addEL(String name, Object elObject) {};
    public void addELObject(String name, ELObject elObject) {};
    public void addELMethod(String name, ELMethod elObject) {}
    //------------------------------------------------------------------------------
    public Object get(Object key) {
        return super.get(key);
    }
    public Object put(Object key, Object value) {
        // TODO Auto-generated method stub
        return super.put(key, value);
    }
    //------------------------------------------------------------------------------
    public boolean contains(String name) {
        return this.containsKey(name);
    }
    public void setAttribute(String name, Object value) {}
    public Object getAttribute(String name) {
        return null;
    }
    public void removeAttribute(String name) {}
    public String[] getAttributeNames() {
        return this.keySet().toArray();
    }
    public void clearAttribute() {
        this.clear();
    };
}