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
package org.more.core.classcode;
import java.io.IOException;
import java.util.ArrayList;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassReader;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.ClassWriter;
/**
 * 类生成器，可以通过继承该类重写acceptClass方法来使用classcode基于asm3.2的高级功能。<br/>
 * 该类提供了三个扩展点来方便灵活扩展生成字节码方面的功能。<br/>
 * <b>扩展点一</b>、<br/>
 * 当{@link ClassBuilder#initBuilder(ClassEngine)}方法被ClassEngine调用之后，在结束方法之前该方法会调用
 * {@link ClassBuilder#init(ClassEngine)}方法。这时扩展类可以初始化自己的相关数据代码。<br/>
 * <b>扩展点二</b>、<br/>
 * 当{@link ClassBuilder#builderClass()}方法被ClassEngine调用之后，在结束方法返回{@link ClassConfiguration}
 * 之前该方法会调用{@link ClassBuilder#builder(ClassEngine)}方法。这时扩展类可以构造其它class或者改写原有字节码。
 * 处于性能上的考虑我优先推荐下面这种扩展方式，因为下面的这种改写字节码的扩展方式是参与在字节码构造层次中的。<br/>
 * <b>扩展点三</b>、<br/>
 * {@link ClassBuilder#acceptClass(ClassVisitor)}方法是一个较为优雅的扩展方式。该方法会在builderClass()方法
 * 构造visitor环调用期间发生。builderClass在生成字节码时使用的是ASM3.2提供的visitor模式，该方法的参数是最终要
 * 写入的visitor。注意使用该扩展方式必须要熟悉ASM3.2框架。visitor环的层次关系是这样的：<br/>
 * <b>第一环</b>，ASM Write；<b>第二环</b>，用户扩展；<b>第三环</b>，ClassBuilder；<b>第四环</b>，ASM Read
 * @version 2010-9-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassBuilder {
    /**该字段会被getClassBytes()方法*/
    protected byte[]               newClassBytes         = null; //新类的字节码。
    private ClassEngine            classEngine           = null; //Class引擎。
    //property
    private String                 asmClassName          = null; //新类的字节码名。
    private String                 asmSuperClassName     = null; //父类的字节码名。
    private String[]               delegateString        = null; //委托类型By ASM
    private Class<?>[]             delegateType          = null; //委托类型By Class
    private String[]               simpleFields          = null; //简单属性
    private String[]               delegateFields        = null; //委托属性
    //aop
    private AopInvokeFilter[]      aopFilter             = null; //aop过滤器对象集合。
    private AopBeforeListener[]    aopBeforeListeners    = null; //before切面事件监听器。
    private AopReturningListener[] aopReturningListeners = null; //returning切面事件监听器。
    private AopThrowingListener[]  aopThrowingListeners  = null; //throwing切面事件监听器。
    //======================================================================================Get/Set
    /**获取使用的Class引擎。*/
    public ClassEngine getClassEngine() {
        return this.classEngine;
    }
    /**返回新类的名称(字节码形式)。*/
    public String getAsmClassName() {
        return this.asmClassName;
    }
    /**返回父类的名称(字节码形式)。*/
    public String getAsmSuperClassName() {
        return this.asmSuperClassName;
    };
    /**返回配置的{@link AopInvokeFilter}数组，该对象是在{@link ClassEngine}类中配置。*/
    public AopInvokeFilter[] getAopFilter() {
        return this.aopFilter;
    }
    /**返回配置的{@link AopBeforeListener}数组，该对象是在{@link ClassEngine}类中配置。*/
    public AopBeforeListener[] getAopBeforeListeners() {
        return this.aopBeforeListeners;
    }
    /**返回配置的{@link AopReturningListener}数组，该对象是在{@link ClassEngine}类中配置。*/
    public AopReturningListener[] getAopReturningListeners() {
        return this.aopReturningListeners;
    }
    /**返回配置的{@link AopThrowingListener}数组，该对象是在{@link ClassEngine}类中配置。*/
    public AopThrowingListener[] getAopThrowingListeners() {
        return this.aopThrowingListeners;
    }
    /**返回委托接口数组的字节码形式。*/
    public String[] getDelegateString() {
        return this.delegateString;
    }
    /**返回委托接口数组。*/
    public Class<?>[] getDelegateType() {
        return this.delegateType;
    }
    /**返回附加的属性名数组。*/
    public String[] getSimpleFields() {
        return this.simpleFields;
    }
    /**返回附加的委托属性的属性名数组。*/
    public String[] getDelegateFields() {
        return this.delegateFields;
    }
    //======================================================================================Is
    /** 返回一个boolean，该值决定了是否输出委托，委托实际上就是一个接口实现。如果getDelegateString()返回null则该方法返回true，否则返回false。 */
    public boolean isAddDelegate() {
        return this.getDelegateString() != null;
    }
    /**
     * 返回一个boolean，该值决定了是否输出额外的附加属性，这些属性包括了简单属性和代理属性。
     * 如果getSimpleFields()和getDelegateFields()同时返回null则该方法返回true，否则返回false。
     */
    public boolean isAddFields() {
        if (this.getSimpleFields() == null && this.getDelegateFields() == null)
            return false;
        else
            return true;
    }
    /**
     * 返回一个boolean，该值可以决定是否渲染aop的装配。builderClass方法调用时是否改造输出aop支持的代码就是通过该方法来判定。
     * 如果getAopFilter()、getAopBeforeListeners()、getAopReturningListeners()、getAopThrowingListeners()返回值都是null时，该方法返回值将会是false否则是true。
     */
    public boolean isRenderAop() {
        if (this.getAopFilter() == null && this.getAopBeforeListeners() == null && this.getAopReturningListeners() == null && this.getAopThrowingListeners() == null)
            return false;
        else
            return true;
    }
    //=======================================================================================Method
    /**获取已经生成的字节码数组。*/
    public byte[] getClassBytes() {
        return this.newClassBytes;
    }
    //======================================================================================Builder
    /**初始化构造器。*/
    public final void initBuilder(ClassEngine classEngine) {
        this.newClassBytes = null; //新类的字节码。
        this.classEngine = null; //Class引擎。
        this.asmClassName = null; //新类的字节码名。
        this.asmSuperClassName = null; //父类的字节码名。
        this.delegateString = null; //委托类型By ASM
        this.delegateType = null; //委托类型By Class
        this.simpleFields = null; //简单属性
        this.delegateFields = null; //委托属性
        this.aopFilter = null; //aop过滤器对象集合。
        this.aopBeforeListeners = null; //before切面事件监听器。
        this.aopReturningListeners = null; //returning切面事件监听器。
        this.aopThrowingListeners = null; //throwing切面事件监听器。
        //---------------------------------------------------------
        this.classEngine = classEngine;
        //1.config
        this.asmClassName = EngineToos.replaceClassName(classEngine.getClassName());
        this.asmSuperClassName = EngineToos.replaceClassName(classEngine.getSuperClass().getName());
        //2.AOP
        this.aopFilter = classEngine.getAopFilters();
        this.aopBeforeListeners = classEngine.getAopBeforeListeners();
        this.aopReturningListeners = classEngine.getAopReturningListeners();
        this.aopThrowingListeners = classEngine.getAopThrowingListeners();
        //3.Delegate
        Class<?>[] delegateType = classEngine.getDelegates();
        if (delegateType != null) {
            //1
            DelegateStrategy delegateStrategy = this.classEngine.getDelegateStrategy();
            ArrayList<Class<?>> delegateTypeList = new ArrayList<Class<?>>();
            for (int i = 0; i < delegateType.length; i++) {
                if (delegateStrategy.isIgnore(delegateType[i]) == true)
                    continue;
                delegateTypeList.add(delegateType[i]);
            }
            //2
            int size = delegateTypeList.size();
            if (size != 0) {
                this.delegateString = new String[size];
                this.delegateType = new Class<?>[size];
                for (int i = 0; i < delegateTypeList.size(); i++) {
                    Class<?> dType = delegateTypeList.get(i);
                    this.delegateType[i] = dType;
                    this.delegateString[i] = EngineToos.replaceClassName(dType.getName());
                }
            }
        }
        //4.Field
        this.simpleFields = classEngine.getAppendSimplePropertys();
        this.delegateFields = classEngine.getAppendDelegatePropertys();
        //5.init
        this.init(this.classEngine);
    }
    /**调用构建过程构建新的字节码对象。*/
    public final ClassConfiguration builderClass() throws IOException {
        //1.基本信息
        ClassEngine engine = this.classEngine;
        Class<?> superClass = engine.getSuperClass();
        //2.构建visitor环
        //------第一环，写入
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        //------第二环，用户扩展
        ClassAdapter visitor = this.acceptClass(writer);
        //------第三环，Aop
        AopClassAdapter aopAdapter = null;
        if (this.isRenderAop() == true) {
            aopAdapter = new AopClassAdapter((visitor == null) ? writer : visitor, this);
            visitor = aopAdapter;
        }
        //------第四环，Builder
        visitor = (visitor != null) ? new BuilderClassAdapter(visitor, this) : new BuilderClassAdapter(writer, this);
        BuilderClassAdapter builderAdapter = (BuilderClassAdapter) visitor;
        //3.Read
        ClassReader reader = new ClassReader(EngineToos.getClassInputStream(superClass));//创建ClassReader
        reader.accept(visitor, ClassReader.SKIP_DEBUG);
        this.newClassBytes = writer.toByteArray();
        this.newClassBytes = this.builder(this.newClassBytes, this.classEngine);
        if (this.newClassBytes == null)
            return null;
        else
            return new ClassConfiguration(this, builderAdapter, aopAdapter);
    }
    //======================================================================================Builder
    /**
     * {@link ClassBuilder#acceptClass(ClassVisitor)}方法是一个较为优雅的扩展方式。该方法会在builderClass()方法
     * 构造visitor环调用期间发生。builderClass在生成字节码时使用的是ASM3.2提供的visitor模式，该方法的参数是最终要
     * 写入的visitor。注意使用该扩展方式必须要熟悉ASM3.2框架。visitor环的层次关系是这样的：<br/>
     * <b>第一环</b>，ASM Write；<b>第二环</b>，用户扩展；<b>第三环</b>，Aop；<b>第四环</b>，ASM Read
     */
    protected ClassAdapter acceptClass(final ClassWriter classVisitor) {
        return null;
    }
    /**
     * 当{@link ClassBuilder#initBuilder(ClassEngine)}方法被ClassEngine调用之后，在结束方法之前该方法会调用
     * {@link ClassBuilder#init(ClassEngine)}方法。这时子类可以通过重写该方法来初始化自己的相关数据。
     */
    protected void init(final ClassEngine classEngine) {}
    /**
     * 当{@link ClassBuilder#builderClass()}方法被ClassEngine调用之后，在结束方法返回{@link ClassConfiguration}
     * 之前该方法会调用{@link ClassBuilder#builder(ClassEngine)}方法。这时子类可以通过重写该方法来构造其它class或者改写原有字节码。
     * @param classEngine 使用的字节码引擎对象。
     */
    protected byte[] builder(final byte[] newClassBytes, final ClassEngine classEngine) {
        return newClassBytes;
    }
}