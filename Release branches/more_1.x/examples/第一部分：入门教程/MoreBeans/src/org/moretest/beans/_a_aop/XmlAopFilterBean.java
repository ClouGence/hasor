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
package org.moretest.beans._a_aop;
import org.more.core.classcode.AOPFilterChain;
import org.more.core.classcode.AOPInvokeFilter;
import org.more.core.classcode.AOPMethods;
/**
 * 
 * @version 2010-1-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlAopFilterBean implements AOPInvokeFilter {
    private static int index = 0;
    private int        i     = 0;
    public XmlAopFilterBean() {
        i = XmlAopFilterBean.index;
        XmlAopFilterBean.index++;
    }
    @Override
    public Object doFilter(Object target, AOPMethods methods, Object[] args, AOPFilterChain chain) throws Throwable {
        System.out.println("method [" + methods.getPropxyMethod().getName() + "]begin-" + i);
        Object obj = chain.doInvokeFilter(target, methods, args);
        System.out.println("method [" + methods.getPropxyMethod().getName() + "]end" + i);
        return obj;
    }
}