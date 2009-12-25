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
/**  */
package org.more.util.attribute;
import org.more.NoDefinitionException;
import org.more.ReadOnlyException;
/**
 *    保持属性接口。该接口扩展了Attribute接口，并且解决了属性保留的问题。使得属性在被删除或者
 * 被替换时可以确定其行为。通过该接口可以将属性设置为保持属性或必须属性。
 *    保持属性：保持属性是指被设置成保持的属性是否可以更改其属性值。这一点与ExtAttribute接口的
 * ReplaceMode_Throw策略很像。不同的是ExtAttribute接口是针对所有属性，而KeepAttribute接口
 * 是针对一个属性。被设置成为保持的属性可以通过removeAttribute方法删除掉。当删除一个拥有保持
 * 特性的属性时，会同时删除其保持特性。
 *    必须属性：必须属性是指该属性可以被任意设置但是不可以被删除。利用必须属性可以保证某些情况下
 * 系统必须能够访问到某些属性，而且这些属性必须不能不存在。
 *    提示：保持特性和必须特性可以同时作用到一个属性上。
 * Date : 2009-4-28
 * @author 赵永春
 */
public interface IKeepAttribute extends IAttribute {
    /**
     * 设置属性，如果属性已经被设置为保持特性则会引发ReadOnlyException异常。
     * @param name 要保存的属性名。
     * @param value 要保存的属性值。
     * @throws ReadOnlyException 如果被设置的属性拥有保持特性则引发该异常。
     */
    public void setAttribute(String name, Object value) throws ReadOnlyException;
    /**
     * 从现有属性集合中删除指定属性。如果被删除的属性拥有必须特性则会引发MustException异常。
     * @param name 要删除的属性名称。
     * @throws UnsupportedOperationException 如果被删除的属性拥有必须特性则引发该异常。
     */
    public void removeAttribute(String name) throws UnsupportedOperationException;
    /**
     * 设置属性是否是保持的。如果试图向一个不存在的属性设置保持属性时引发NoDefinitionException异常。
     * @param attName 要设置保持特性的属性名。
     * @param isKeep 如果设置值为true则表示设置其保持特性，否则就取消其保持特性。
     * @throws NoDefinitionException 未定义异常，如果试图向一个不存在的属性设置保持属性时引发。
     */
    public void setKeep(String attName, boolean isKeep) throws NoDefinitionException;
    /**
     * 测试某个属性是否拥有保持特性。如果测试一个不存在的属性该方法将始终返回false。
     * @param attName 被测试的属性名。
     * @return 返回某个属性是否拥有保持特性。
     */
    public boolean isKeep(String attName);
    /**
     * 设置属性是否是必须的。如果试图向一个不存在的属性设置保持属性时引发NoDefinitionException异常。
     * @param attName 要设置必须特性的属性名。
     * @param isMaster 如果设置值为true则表示设置其必须特性，否则就取消其必须特性。
     * @throws NoDefinitionException 未定义异常，如果试图向一个不存在的属性设置必须属性时引发。
     */
    public void setMaster(String attName, boolean isMaster) throws NoDefinitionException;
    /**
     * 测试某个属性是否拥有必须特性。如果测试一个不存在的属性该方法将始终返回false。
     * @param attName 被测试的属性名。
     * @return 返回某个属性是否拥有必须特性。
     */
    public boolean isMaster(String attName);
}
