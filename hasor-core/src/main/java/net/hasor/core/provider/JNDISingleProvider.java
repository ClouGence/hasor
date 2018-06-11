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
package net.hasor.core.provider;
import net.hasor.core.Provider;
import net.hasor.utils.ExceptionUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
/**
 * JNDI 级别的单例对象的{@link Provider}封装形式。
 * @version : 2014年7月8日
 * @author 赵永春 (zyc@hasor.net)
 */
public class JNDISingleProvider<T> extends SingleProvider<T> {
    private final Context jndiContext;
    private       String  jndiName;
    //
    public JNDISingleProvider(String jndiName, Provider<T> provider) throws NamingException {
        super(provider);
        this.jndiName = jndiName;
        jndiContext = new InitialContext();
    }
    //
    protected T newInstance(Provider<T> provider) {
        try {
            Object lookup = jndiContext.lookup(this.jndiName);
            if (lookup != null) {
                return (T) lookup;
            }
            //
            T target = provider.get();
            jndiContext.bind(this.jndiName, target);
            return target;
        } catch (NamingException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    public String toString() {
        return "ClassLoaderSingleProvider-> jndi:" + jndiName;
    }
}