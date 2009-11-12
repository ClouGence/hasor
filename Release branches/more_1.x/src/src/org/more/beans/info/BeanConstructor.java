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
package org.more.beans.info;
import org.more.util.attribute.AttBase;
/**
 * 
 * Date : 2009-11-10
 * @author Administrator
 */
public class BeanConstructor extends AttBase {
    /**  */
    private static final long      serialVersionUID = 3461453713657581453L;
    //
    private BeanConstructorParam[] paramTypes       = null;                //
    //=================================================================
    public BeanConstructorParam[] getParamTypes() {
        return paramTypes;
    }
    public void setParamTypes(BeanConstructorParam[] paramTypes) {
        this.paramTypes = paramTypes;
    }
}