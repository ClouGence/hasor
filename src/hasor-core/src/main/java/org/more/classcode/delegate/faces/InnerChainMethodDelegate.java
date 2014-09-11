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
package org.more.classcode.delegate.faces;
import java.lang.reflect.Method;
/**
 * 
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerChainMethodDelegate implements MethodDelegate {
    private MethodDelegate propertyDelegate = null;
    //
    public InnerChainMethodDelegate(String delegateID, ClassLoader loader) {
        System.out.println();
    }
    //
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
}