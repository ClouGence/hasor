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
package org.more.hypha.beans.assembler.builder;
import java.io.IOException;
import org.more.hypha.beans.define.VariableBeanDefine;
import org.more.hypha.commons.engine.AbstractBeanBuilder;
/**
 * 
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class VariableBeanBuilder extends AbstractBeanBuilder<VariableBeanDefine> {
    public boolean canCache() {
        return true;
    };
    public boolean canBuilder() {
        return true;
    };
    public boolean ifDefaultBeanCreateMode() {
        return true;
    };
    //---------------------------------------------------------
    public byte[] loadBeanBytes(VariableBeanDefine define) throws IOException {
        // TODO Auto-generated method stub
        return null;
    };
    public Object createBean(VariableBeanDefine define, Object[] params) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    };
    public Object builderBean(Object obj, VariableBeanDefine define) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    };
    //---------------------------------------------------------
};