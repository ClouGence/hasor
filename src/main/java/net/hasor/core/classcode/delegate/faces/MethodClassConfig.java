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
package net.hasor.core.classcode.delegate.faces;
import net.hasor.core.classcode.AbstractClassConfig;
import net.hasor.core.classcode.asm.ClassVisitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 *
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class MethodClassConfig extends AbstractClassConfig {
    private Map<Class<?>, InnerMethodDelegateDefine> newDelegateMap = null; //方法委托
    //
    /**创建{@link MethodClassConfig}类型对象。 */
    public MethodClassConfig() {
        super(DefaultSuperClass);
    }
    /**创建{@link MethodClassConfig}类型对象。 */
    public MethodClassConfig(Class<?> superClass) {
        super(superClass);
    }
    /**创建{@link MethodClassConfig}类型对象。 */
    public MethodClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        super(superClass, parentLoader);
    }
    //
    protected String initClassName() {
        return this.getSuperClass().getName() + "$M_" + index();
    }
    @Override
    protected ClassVisitor buildClassVisitor(ClassVisitor parentVisitor) {
        return new MethodDelegateClassAdapter(parentVisitor, this);
    }
    //
    /**是否包含改变*/
    public boolean hasChange() {
        return (this.newDelegateMap == null) ? false : (!this.newDelegateMap.isEmpty());
    }
    /**
     * 向新类中添加一个委托接口实现，该委托接口中的所有方法均通过委托对象代理处理。如果委托接口中有方法与基类的方法冲突时。
     * 新生成的委托方法则会丢弃委托接口中的方法去保留基类方法。这在java中也是相当于实现，但是更重要的是保护了基类。
     * 如果重复添加同一个接口则该接口将被置于最后一次添加。注意：如果试图添加一个非接口类型则会引发异常。
     * @param appendInterface 要附加的接口。
     * @param delegate 委托接口的方法处理委托。
     */
    public void addDelegate(final Class<?> appendInterface, final MethodDelegate delegate) {
        //1.参数判断
        if (appendInterface.isInterface() == false || delegate == null) {
            throw new ClassCastException("委托不是一个有效的接口类型，或者MethodDelegate类型参数为空。");
        }
        //2.测试该接口是否已经得到实现
        if (appendInterface.isAssignableFrom(this.getSuperClass()) == true) {
            throw new IllegalStateException("委托接口已经被实现。");
        }
        //3.检测重复,附加接口实现
        if (this.newDelegateMap == null) {
            this.newDelegateMap = new LinkedHashMap<Class<?>, InnerMethodDelegateDefine>();
        }
        if (this.newDelegateMap.containsKey(appendInterface) == false) {
            InnerMethodDelegateDefine define = new InnerMethodDelegateDefine(appendInterface, delegate);
            this.newDelegateMap.put(appendInterface, define);
        }
    }
    InnerMethodDelegateDefine[] getNewDelegateList() {
        if (this.newDelegateMap == null) {
            return new InnerMethodDelegateDefine[0];
        }
        InnerMethodDelegateDefine[] newProperty = this.newDelegateMap.values()//
                .toArray(new InnerMethodDelegateDefine[this.newDelegateMap.size()]);
        return newProperty;
    }
    //
    //
    //bytecode called.
    private Map<String, InnerMethodDelegateDefine> $methodMapping = null;
    private void initMapping() {
        if (this.$methodMapping != null) {
            return;
        }
        this.$methodMapping = new HashMap<String, InnerMethodDelegateDefine>();
        InnerMethodDelegateDefine[] defineList = this.getNewDelegateList();
        for (InnerMethodDelegateDefine mDefine : defineList) {
            String tmName = mDefine.getFaces().getName();
            this.$methodMapping.put(tmName, mDefine);
        }
    }
    InnerMethodDelegateDefine getMethodDelegate(String delegateClassName) {
        this.initMapping();
        return this.$methodMapping.get(delegateClassName);
    }
}