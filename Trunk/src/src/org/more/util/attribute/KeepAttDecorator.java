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
package org.more.util.attribute;
import java.util.HashMap;
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.ReadOnlyException;
/**
 * 属性保持装饰器，该装饰器实现了IKeepAttribute接口的功能。
 * Date : 2009-4-30
 * @author 赵永春
 */
public class KeepAttDecorator extends AbstractAttDecorator implements IKeepAttribute {
    /** 保存属性特性的Map */
    private Map<String, KeepAttDecorator.KeepAttStruct> map = null;
    /**
     * 属性特性内部类，该类是用于保存属性特性数据。
     * Date : 2009-4-29
     * @author 赵永春
     */
    private static class KeepAttStruct {
        /**必须特性*/
        private boolean master = false;
        /**保持特性*/
        private boolean keep   = false;
    }
    /**
     * 构造一个属性装饰器，该装饰器的主要功能是增加属性对象对替换策略的支持。替换策略是由IExtAttribute接口定义。
     * 该方法将采用默认策略IExtAttribute.ReplaceMode_Replace。
     * @param source 要装饰的目标属性对象。
     */
    public KeepAttDecorator(IAttribute source) {
        super(source);
        this.map = new HashMap<String, KeepAttDecorator.KeepAttStruct>();
    }
    @Override
    public void removeAttribute(String name) throws UnsupportedOperationException {
        if (this.getSource().contains(name) == true)
            if (this.map.get(name).master == true)
                throw new UnsupportedOperationException("不能删除属性 " + name + " ，该属性具备必须特性。");
        this.getSource().removeAttribute(name);
        this.map.remove(name);
    }
    @Override
    public void setAttribute(String name, Object value) throws ReadOnlyException {
        if (this.getSource().contains(name) == true)
            if (this.map.get(name).keep == true)
                throw new ReadOnlyException("属性 " + name + " 具备保持特性不能接受新的属性值。");
        this.getSource().setAttribute(name, value);
        this.map.put(name, new KeepAttStruct());
    }
    @Override
    public boolean isKeep(String attName) {
        return (this.getSource().contains(attName) == false) ? false : this.map.get(attName).keep;
    }
    @Override
    public boolean isMaster(String attName) {
        return (this.getSource().contains(attName) == false) ? false : this.map.get(attName).master;
    }
    @Override
    public void setKeep(String attName, boolean isKeep) throws NoDefinitionException {
        if (this.getSource().contains(attName) == false)
            throw new NoDefinitionException("不能给不存在的属性 " + attName + " 设置保持特性");
        else
            this.map.get(attName).keep = isKeep;
    }
    @Override
    public void setMaster(String attName, boolean isMaster) throws NoDefinitionException {
        if (this.getSource().contains(attName) == false)
            throw new NoDefinitionException("不能给不存在的属性 " + attName + " 设置必须特性");
        else
            this.map.get(attName).master = isMaster;
    }
}
