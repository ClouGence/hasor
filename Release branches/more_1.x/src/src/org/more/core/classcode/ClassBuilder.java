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
 *
 * @version 2010-8-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassBuilder {
    protected byte[]               newClassBytes         = null; //新类的字节码。
    private ClassEngine            classEngine           = null;
    //
    private String                 asmClassName          = null;
    private String                 asmSuperClassName     = null;
    private String[]               delegateString        = null;
    private Class<?>[]             delegateType          = null;
    private String[]               simpleFields          = null;
    private String[]               delegateFields        = null;
    //
    private AopInvokeFilter[]      aopFilter             = null;
    private AopBeforeListener[]    aopBeforeListeners    = null;
    private AopReturningListener[] aopReturningListeners = null;
    private AopThrowingListener[]  aopThrowingListeners  = null;
    //==================================================================================Constructor
    //======================================================================================Get/Set
    public ClassEngine getClassEngine() {
        return this.classEngine;
    }
    public String getAsmClassName() {
        return this.asmClassName;
    }
    public String getAsmSuperClassName() {
        return this.asmSuperClassName;
    };
    public AopInvokeFilter[] getAopFilter() {
        return this.aopFilter;
    }
    public AopBeforeListener[] getAopBeforeListeners() {
        return this.aopBeforeListeners;
    }
    public AopReturningListener[] getAopReturningListeners() {
        return this.aopReturningListeners;
    }
    public AopThrowingListener[] getAopThrowingListeners() {
        return this.aopThrowingListeners;
    }
    public String[] getDelegateString() {
        return this.delegateString;
    }
    public Class<?>[] getDelegateType() {
        return this.delegateType;
    }
    public String[] getSimpleFields() {
        return this.simpleFields;
    }
    public String[] getDelegateFields() {
        return this.delegateFields;
    }
    public boolean isAddDelegate() {
        return this.delegateString != null;
    }
    public boolean isAddFields() {
        if (this.simpleFields == null && this.delegateFields == null)
            return false;
        else
            return true;
    }
    public boolean isRenderAop() {
        if (this.aopFilter == null && this.aopBeforeListeners == null && this.aopReturningListeners == null && this.aopThrowingListeners == null)
            return false;
        else
            return true;
    }
    //=======================================================================================Method
    public byte[] getClassBytes() {
        return this.newClassBytes;
    }
    //======================================================================================Builder
    /**准备信息，检查信息正确性。使用策略。*/
    public final void initBuilder(ClassEngine classEngine) {
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
        //
        this.builderClass(this.classEngine);
    }
    /**构建*/
    public final ClassConfiguration builderClass() throws IOException {
        this.builderClass(this.classEngine);
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
        if (this.newClassBytes == null)
            return null;
        else
            return new ClassConfiguration(this, builderAdapter, aopAdapter);
    }
    //======================================================================================Builder
    /** 子类决定最终的ClassAdapter，子类ClassAdapter可以更自由的控制字节码注意子类在重写该方法时一定要使用classWriter作为最终的字节码输出对象。*/
    protected ClassAdapter acceptClass(final ClassVisitor classVisitor) {
        return null;
    }
    protected void init(ClassEngine classEngine) {}
    protected void builderClass(ClassEngine classEngine) {}
}