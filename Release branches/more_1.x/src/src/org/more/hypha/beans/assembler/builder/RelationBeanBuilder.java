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
import org.more.hypha.beans.define.RelationBeanDefine;
import org.more.hypha.commons.engine.AbstractBeanBuilder;
/**
 * 
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class RelationBeanBuilder extends AbstractBeanBuilder<RelationBeanDefine> {
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
    public byte[] loadBeanBytes(RelationBeanDefine define) throws IOException {
        // TODO Auto-generated method stub
        return null;
    };
    public Object createBean(RelationBeanDefine define, Object[] params) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    };
    public Object builderBean(Object obj, RelationBeanDefine define) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    };
    //---------------------------------------------------------
};