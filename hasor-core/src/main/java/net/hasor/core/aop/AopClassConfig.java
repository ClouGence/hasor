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
package net.hasor.core.aop;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.PropertyDelegate;
import net.hasor.core.aop.InnerDelegateInvocation.DelegateInfo;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.hasor.utils.asm.Opcodes.*;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class AopClassConfig {
    /** 默认超类java.lang.Object */
    private final          Class<?>                           superClass;
    private final          AopClassLoader                     parentLoader;
    private final          String                             className;
    private                byte[]                             classBytes;
    private                Class<?>                           classType;
    //
    private final          Map<String, DelegateInfo>          delegatePropertyMap = new HashMap<>();
    private final          List<InnerMethodInterceptorDefine> interceptorList     = new ArrayList<>();
    private final          Map<String, MethodInterceptor[]>   interceptorMap      = new HashMap<>();
    private final          Map<String, Method>                interceptorMethod   = new HashMap<>();
    private static final   AtomicLong                         spinIndex           = new AtomicLong(0);
    protected static final String                             aopMethodSuffix     = "aop$";
    protected static final String                             aopClassSuffix      = "$Auto$";
    private                File                               classWritePath;

    /** 创建{@link AopClassConfig}类型对象 */
    public AopClassConfig() {
        this(BasicObject.class);
    }

    /** 创建{@link AopClassConfig}类型对象 */
    public AopClassConfig(Class<?> superClass) {
        this(superClass, superClass.getClassLoader());
    }

    /** 创建{@link AopClassConfig}类型对象 */
    public AopClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        this.superClass = (superClass == null) ? BasicObject.class : superClass;
        this.className = this.superClass.getName() + aopClassSuffix + spinIndex.getAndIncrement();
        this.classBytes = null;
        if (parentLoader instanceof AopClassLoader) {
            this.parentLoader = (AopClassLoader) parentLoader;
        } else {
            this.parentLoader = new AopClassLoader(parentLoader);
        }
    }
    // --------------------------------------------------------------------------------------------

    /** 动态添加一个属性，并且生成可以属性的get/set方法 */
    public <T> void addProperty(String propertyName, Class<? extends T> propertyType) {
        this.addProperty(propertyName, propertyType, ReadWriteType.ReadWrite);
    }

    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法
     * @param propertyName 属性名
     * @param propertyType 属性类型
     * @param rwType 读写权限
     */
    public <T> void addProperty(String propertyName, Class<? extends T> propertyType, ReadWriteType rwType) {
        Object defaultValue = BeanUtils.getDefaultValue(propertyType);
        SimplePropertyDelegate delegate = new SimplePropertyDelegate(defaultValue);
        this.addProperty(propertyName, propertyType, InstanceProvider.of(delegate), rwType);
    }

    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法
     * @param propertyName 属性名
     * @param propertyType 属性类型
     * @param delegate 属性默认值
     */
    public <T> void addProperty(String propertyName, Class<? extends T> propertyType, Supplier<? extends PropertyDelegate> delegate) {
        this.addProperty(propertyName, propertyType, delegate, ReadWriteType.ReadWrite);
    }

    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法
     * @param propertyName 属性名
     * @param propertyType 属性类型
     * @param delegate 属性默认值
     * @param rwType 读写权限
     */
    public <T> void addProperty(String propertyName, Class<? extends T> propertyType, Supplier<? extends PropertyDelegate> delegate, ReadWriteType rwType) {
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("args propertyName is null.");
        }
        if (propertyType == null) {
            throw new IllegalArgumentException("args propertyType is null.");
        }
        // 如果存在这个属性，则抛出异常
        if (BeanUtils.hasPropertyOrField(propertyName, this.getSuperClass())) {
            throw new IllegalStateException(propertyName + " already exists");
        }
        this.delegatePropertyMap.put(propertyName, new DelegateInfo(propertyType, delegate, rwType));
    }

    Supplier<? extends PropertyDelegate> findPropertyDelegate(String name) {
        return this.delegatePropertyMap.get(name).delegateSupplier;
    }
    // --------------------------------------------------------------------------------------------

    /** 添加Aop拦截器 */
    public void addAopInterceptors(Predicate<Method> aopMatcher, MethodInterceptor... aopInterceptor) {
        for (MethodInterceptor aop : aopInterceptor) {
            this.addAopInterceptor(aopMatcher, aop);
        }
    }

    /** 添加Aop拦截器 */
    public void addAopInterceptor(MethodInterceptor aopInterceptor) {
        this.addAopInterceptor(target -> true, aopInterceptor);
    }

    /** 添加Aop拦截器 */
    public void addAopInterceptor(Predicate<Method> aopMatcher, MethodInterceptor aopInterceptor) {
        if (aopMatcher == null) {
            throw new IllegalArgumentException("args aopMatcher is null.");
        }
        if (aopInterceptor == null) {
            throw new IllegalArgumentException("args aopInterceptor is null.");
        }
        this.interceptorList.add(new InnerMethodInterceptorDefine(aopMatcher, aopInterceptor));
    }
    // --------------------------------------------------------------------------------------------

    /** 根据方法查找这个方法的所有拦截器 */
    MethodInterceptor[] findInterceptor(String tmDesc) {
        return this.interceptorMap.get(tmDesc);
    }

    /** 是否支持Aop */
    public boolean isSupport() {
        return AsmTools.isSupport(this.getSuperClass());
    }

    /** 取得字节码信息 */
    public byte[] getBytes() {
        return this.classBytes;
    }

    /** 父类类型 */
    public Class<?> getSuperClass() {
        return superClass;
    }

    /** 新类类名 */
    public String getClassName() {
        return this.className;
    }

    /** 新类类名 */
    public String getSimpleName() {
        if (this.className == null) {
            return null;
        }
        return this.className.substring(this.className.lastIndexOf(".") + 1);
    }

    /**是否包含改变*/
    public boolean hasChange() {
        return !this.interceptorList.isEmpty() || !this.delegatePropertyMap.isEmpty();
    }

    protected void initBuild() {
        // . 构建 interceptorMap
        Method[] targetMethodArrays = this.getSuperClass().getMethods();
        for (Method targetMethod : targetMethodArrays) {
            int dataModifiers = targetMethod.getModifiers();
            if (AsmTools.checkOr(dataModifiers, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)) {
                continue;
            }
            //
            List<MethodInterceptor> interceptorList = new ArrayList<>();
            for (InnerMethodInterceptorDefine define : this.interceptorList) {
                if (!define.test(targetMethod)) {
                    continue;
                }
                interceptorList.add(define);
            }
            //
            if (!interceptorList.isEmpty()) {
                String interceptorMethodDesc = AsmTools.toAsmFullDesc(targetMethod);
                MethodInterceptor[] aopArrays = interceptorList.toArray(new MethodInterceptor[0]);
                this.interceptorMap.put(interceptorMethodDesc, aopArrays);
                this.interceptorMethod.put(interceptorMethodDesc, targetMethod);
            }
        }
    }

    /** 调用ClassLoader，生成字节码并装载它 */
    public synchronized <T> Class<? extends T> buildClass() throws IOException, ClassNotFoundException, NoSuchMethodException {
        if (this.classType != null) {
            return (Class<? extends T>) this.classType;
        }
        if (!this.hasChange()) {
            return (Class<? extends T>) this.superClass;
        }
        if (!isSupport()) {
            throw new IOException("class in package java or javax , does not support.");
        }
        this.initBuild();
        String thisClassName = AsmTools.replaceClassName(this.getClassName());
        String superClassName = AsmTools.replaceClassName(this.getSuperClass());
        String exceptionUtilsName = AsmTools.replaceClassName(ExceptionUtils.class);
        Map<String, Integer> indexMap = new HashMap<>();
        //
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V1_6, ACC_PUBLIC + ACC_SUPER, thisClassName, null, superClassName, new String[] {//
                AsmTools.replaceClassName(DynamicClass.class)//
        });
        // .构造方法
        Constructor<?>[] constructorArray = this.getSuperClass().getConstructors();
        for (Constructor<?> constructor : constructorArray) {
            String[] asmParams = AsmTools.splitAsmType(AsmTools.toAsmType(constructor.getParameterTypes()));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
            String[] throwStrArray = AsmTools.replaceClassName(constructor.getExceptionTypes());
            String paramsStr = "(" + AsmTools.toAsmType(constructor.getParameterTypes()) + ")V";
            //
            AtomicInteger variableIndexCounters = new AtomicInteger(0);
            Map<String, Integer> paramIndexMap = new LinkedHashMap<>();
            paramIndexMap.put("this", 0);
            for (int i = 0; i < asmParams.length; i++) {
                paramIndexMap.put("args" + i, variableIndexCounters.incrementAndGet());
                if ("D".equals(asmParams[i])) {
                    variableIndexCounters.incrementAndGet();// double 需要额外增加1
                }
                if ("J".equals(asmParams[i])) {
                    variableIndexCounters.incrementAndGet();// long 需要额外增加1
                }
            }
            //
            Label startBlock = new Label();
            Label endBlock = new Label();
            //
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", paramsStr, null, throwStrArray);
            mv.visitCode();
            mv.visitLabel(startBlock);
            mv.visitVarInsn(ALOAD, paramIndexMap.get("this"));
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                mv.visitVarInsn(AsmTools.getLoad(AsmTools.toAsmType(parameterTypes[i])), paramIndexMap.get("args" + i));
            }
            mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", paramsStr, false);
            mv.visitInsn(RETURN);
            mv.visitLabel(endBlock);
            mv.visitLocalVariable("this", "L" + thisClassName + ";", null, startBlock, endBlock, paramIndexMap.get("this"));
            for (int i = 0; i < parameterTypes.length; i++) {
                mv.visitLocalVariable("args" + i, AsmTools.toAsmType(parameterTypes[i]), null, startBlock, endBlock, paramIndexMap.get("args" + i));
            }
            int maxStack = parameterTypes.length + 1;
            mv.visitMaxs(maxStack, maxStack);
            mv.visitEnd();
        }
        {//.静态代码块 static
            FieldVisitor fv1 = classWriter.visitField(ACC_PRIVATE + ACC_STATIC, "proxyMethod", AsmTools.toAsmType(Method[].class), null, null);
            fv1.visitEnd();
            FieldVisitor fv2 = classWriter.visitField(ACC_PRIVATE + ACC_STATIC, "targetMethod", AsmTools.toAsmType(Method[].class), null, null);
            fv2.visitEnd();
            //
            int superClassIndex = 0;// 0 位置是 superClass
            int thisClassIndex = 1;// 0 位置是 superClass
            MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label tryStartLabel = new Label();
            Label tryStartCodeLabel = new Label();
            Label tryCacheLabel = new Label();
            Label tryEndLabel = new Label();
            Label returnLabel = new Label();
            Label cacheStartCodeLabel = new Label();
            //
            mv.visitTryCatchBlock(tryStartLabel, tryCacheLabel, tryEndLabel, AsmTools.replaceClassName(Throwable.class));
            mv.visitLabel(tryStartLabel);
            mv.visitLdcInsn(Type.getType(AsmTools.toAsmType(this.getSuperClass())));
            mv.visitVarInsn(ASTORE, superClassIndex);
            mv.visitLdcInsn(Type.getType("L" + thisClassName + ";"));
            mv.visitVarInsn(ASTORE, thisClassIndex);
            //
            mv.visitLabel(tryStartCodeLabel);
            mv.visitIntInsn(BIPUSH, this.interceptorMethod.size());
            mv.visitTypeInsn(ANEWARRAY, AsmTools.replaceClassName(Method.class));
            mv.visitFieldInsn(PUTSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
            mv.visitIntInsn(BIPUSH, this.interceptorMethod.size());
            mv.visitTypeInsn(ANEWARRAY, AsmTools.replaceClassName(Method.class));
            mv.visitFieldInsn(PUTSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
            //
            int i = -1;
            String getMethodDesc = AsmTools.toAsmDesc(Class.class.getMethod("getMethod", String.class, Class[].class));
            for (Map.Entry<String, Method> ent : this.interceptorMethod.entrySet()) {
                Method aopMethod = ent.getValue();
                Class<?>[] parameterTypes = aopMethod.getParameterTypes();
                indexMap.put(AsmTools.toAsmFullDesc(aopMethod), ++i);
                //
                // targetMethod[n] = superClass.getMethod("xxxx",new Class[] { xxx,xxx});
                mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD, superClassIndex); // superClass
                mv.visitLdcInsn(interceptorMethod.get(ent.getKey()).getName());
                AsmTools.codeBuilder_2(mv, AsmTools.splitAsmType(AsmTools.toAsmType(parameterTypes)));
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Class.class), "getMethod", getMethodDesc, false);
                mv.visitInsn(AASTORE);
                //
                // targetMethod[n].setAccessible(true);
                mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(AALOAD);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Method.class), "setAccessible", "(Z)V", false);
                //
                // propxyMethod[n] = superClass.getMethod("aop$" + "xxxx",new Class[] { xxx,xxx});
                mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD, thisClassIndex); // superClass
                mv.visitLdcInsn(aopMethodSuffix + interceptorMethod.get(ent.getKey()).getName());
                AsmTools.codeBuilder_2(mv, AsmTools.splitAsmType(AsmTools.toAsmType(parameterTypes)));
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Class.class), "getDeclaredMethod", getMethodDesc, false);
                mv.visitInsn(AASTORE);
                //
                // targetMethod[n].setAccessible(true);
                mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(AALOAD);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Method.class), "setAccessible", "(Z)V", false);
            }
            //
            mv.visitLabel(tryCacheLabel);
            mv.visitJumpInsn(GOTO, returnLabel);
            mv.visitLabel(tryEndLabel);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { AsmTools.replaceClassName(Throwable.class) });
            mv.visitVarInsn(ASTORE, 0);
            mv.visitLabel(cacheStartCodeLabel);
            mv.visitVarInsn(ALOAD, 0);
            String exceptionMethodDesc = AsmTools.toAsmDesc(ExceptionUtils.class.getMethod("toRuntimeException", Throwable.class));
            mv.visitMethodInsn(INVOKESTATIC, exceptionUtilsName, "toRuntimeException", exceptionMethodDesc, false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(returnLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitLocalVariable("superclass", AsmTools.toAsmType(Class.class), "Ljava/lang/Class<*>;", tryStartCodeLabel, tryCacheLabel, 0);
            mv.visitLocalVariable("e", AsmTools.toAsmType(Throwable.class), null, cacheStartCodeLabel, returnLabel, 0);
            mv.visitMaxs(8, 1);
            mv.visitEnd();
        }
        for (Map.Entry<String, Method> ent : this.interceptorMethod.entrySet()) {
            Method aopMethod = ent.getValue();
            String[] asmParams = AsmTools.splitAsmType(AsmTools.toAsmType(aopMethod.getParameterTypes()));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
            String[] throwStrArray = AsmTools.replaceClassName(aopMethod.getExceptionTypes());
            //
            AtomicInteger variableIndexCounters = new AtomicInteger(0);
            Map<String, Integer> paramIndexMap = new LinkedHashMap<>();
            paramIndexMap.put("this", 0);
            for (int i = 0; i < asmParams.length; i++) {
                paramIndexMap.put("args" + i, variableIndexCounters.incrementAndGet());
                if ("D".equals(asmParams[i])) {
                    variableIndexCounters.incrementAndGet();// double 需要额外增加1
                }
                if ("J".equals(asmParams[i])) {
                    variableIndexCounters.incrementAndGet();// long 需要额外增加1
                }
            }
            //
            MethodVisitor replacementVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_FINAL, aopMethodSuffix + aopMethod.getName(), AsmTools.toAsmDesc(aopMethod), AsmTools.toAsmSignature(aopMethod), throwStrArray);
            replacementVisitor.visitCode();
            replacementVisitor.visitVarInsn(ALOAD, 0);
            for (int i = 0; i < asmParams.length; i++) {
                replacementVisitor.visitVarInsn(AsmTools.getLoad(asmParams[i]), paramIndexMap.get("args" + i));
            }
            replacementVisitor.visitMethodInsn(INVOKESPECIAL, superClassName, aopMethod.getName(), AsmTools.toAsmDesc(aopMethod), false);
            replacementVisitor.visitInsn(AsmTools.getReturn(AsmTools.toAsmType(aopMethod.getReturnType())));
            replacementVisitor.visitMaxs(-1, -1);
            replacementVisitor.visitEnd();
            //
            //
            int paramsIndexMark = variableIndexCounters.get();
            paramIndexMap.put("paramObjects", variableIndexCounters.incrementAndGet());
            paramIndexMap.put("returnData", variableIndexCounters.incrementAndGet());
            paramIndexMap.put("e", paramsIndexMark + 1);// is in cache so recount
            //
            Label tryStartLabel = new Label();
            Label tryCacheLabel = new Label();
            Label tryEndLabel = new Label();
            Label paramObjectsLabel = new Label();
            Label returnDataLabel = new Label();
            Label returnLabel = new Label();
            Label eLabel = new Label();
            //
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, aopMethod.getName(), AsmTools.toAsmDesc(aopMethod), AsmTools.toAsmSignature(aopMethod), throwStrArray);
            mv.visitCode();
            mv.visitTryCatchBlock(tryStartLabel, tryCacheLabel, tryEndLabel, AsmTools.replaceClassName(Throwable.class));
            mv.visitLabel(tryStartLabel);
            //
            // Object[] paramObjects = new Object[] { longValue, doubleValue };
            AsmTools.codeBuilder_1(mv, asmParams, paramIndexMap);
            mv.visitVarInsn(ASTORE, paramIndexMap.get("paramObjects"));
            mv.visitLabel(paramObjectsLabel);
            //
            // Object obj = new InnerAopInvocation("checkBaseType1", targetMethod[0], this, pObjects).proceed();
            mv.visitTypeInsn(NEW, AsmTools.replaceClassName(InnerAopInvocation.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(ent.getKey());
            mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
            mv.visitIntInsn(BIPUSH, indexMap.get(AsmTools.toAsmFullDesc(aopMethod)));
            mv.visitInsn(AALOAD);
            mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
            mv.visitIntInsn(BIPUSH, indexMap.get(AsmTools.toAsmFullDesc(aopMethod)));
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, paramIndexMap.get("this"));
            mv.visitVarInsn(ALOAD, paramIndexMap.get("paramObjects"));
            String initDesc = AsmTools.toAsmType(InnerAopInvocation.class.getConstructor(String.class, Method.class, Method.class, Object.class, Object[].class).getParameterTypes());
            mv.visitMethodInsn(INVOKESPECIAL, AsmTools.replaceClassName(InnerAopInvocation.class), "<init>", "(" + initDesc + ")V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(InnerAopInvocation.class), "proceed", "()Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, paramIndexMap.get("returnData"));
            mv.visitLabel(returnDataLabel);
            // return (List) obj;
            mv.visitVarInsn(ALOAD, paramIndexMap.get("returnData"));
            AsmTools.codeBuilder_3(mv, AsmTools.toAsmType(aopMethod.getReturnType()), tryCacheLabel);
            //
            mv.visitLabel(tryEndLabel);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });
            //
            //            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            //            int finalElseFrameMode = 0;
            //            Class<?>[] exceptionTypes = aopMethod.getExceptionTypes();
            //            for (int i = 0; i < exceptionTypes.length; i++) {
            //                if (i == 0) {
            //                    finalElseFrameMode = Opcodes.F_APPEND;
            //                } else if (i == 1) {
            //                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
            //                    finalElseFrameMode = Opcodes.F_SAME;
            //                } else {
            //                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            //                    finalElseFrameMode = Opcodes.F_SAME;
            //                }
            //                //
            //                Label ifEnd = new Label();
            //                mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
            //                mv.visitTypeInsn(INSTANCEOF, AsmTools.replaceClassName(exceptionTypes[i]));
            //                mv.visitJumpInsn(IFEQ, ifEnd);
            //                mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
            //                mv.visitTypeInsn(CHECKCAST, AsmTools.replaceClassName(exceptionTypes[i]));
            //                mv.visitInsn(ATHROW);
            //                mv.visitLabel(ifEnd);
            //            }
            //            //
            //            if (finalElseFrameMode == Opcodes.F_APPEND) {
            //                mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
            //            } else {
            //                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            //            }
            //
            mv.visitVarInsn(ASTORE, paramIndexMap.get("e"));
            mv.visitLabel(eLabel);
            mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
            String exceptionMethodDesc = AsmTools.toAsmDesc(ExceptionUtils.class.getMethod("toRuntimeException", Throwable.class));
            mv.visitMethodInsn(INVOKESTATIC, AsmTools.replaceClassName(ExceptionUtils.class), "toRuntimeException", exceptionMethodDesc, false);
            mv.visitInsn(ATHROW);
            //
            mv.visitLabel(returnLabel);
            mv.visitLocalVariable("paramObjects", "[Ljava/lang/Object;", null, paramObjectsLabel, tryEndLabel, paramIndexMap.get("paramObjects"));
            mv.visitLocalVariable("returnData", "Ljava/lang/Object;", null, returnDataLabel, tryEndLabel, paramIndexMap.get("returnData"));
            mv.visitLocalVariable("e", "Ljava/lang/Throwable;", null, eLabel, returnLabel, paramIndexMap.get("e"));
            mv.visitLocalVariable("this", "L" + thisClassName + ";", null, tryStartLabel, returnLabel, paramIndexMap.get("this"));
            for (int i = 0; i < asmParams.length; i++) {
                mv.visitLocalVariable("args" + i, asmParams[i], null, tryStartLabel, returnLabel, paramIndexMap.get("args" + i));
            }
            //
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }
        // .代理属性
        for (Map.Entry<String, DelegateInfo> ent : this.delegatePropertyMap.entrySet()) {
            String propertyName = ent.getKey();
            DelegateInfo propertyInfo = ent.getValue();
            String asmType = AsmTools.toAsmType(propertyInfo.propertyType);
            {
                Label sLabel = new Label();
                Label eLabel = new Label();
                String methodName = ((propertyInfo.propertyType == Boolean.TYPE) ? "is" : "get") + StringUtils.firstCharToUpperCase(propertyName);
                String methodDescriptor = "()" + asmType;
                Method propertyMethod = InnerDelegateInvocation.class.getMethod("getProperty", String.class, Object.class, String.class);
                //
                MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDescriptor, null, new String[] { "java/lang/Throwable" });
                mv.visitLabel(sLabel);
                mv.visitLdcInsn(getClassName() + "_get_" + propertyName);
                mv.visitVarInsn(ALOAD, 0);// this
                mv.visitLdcInsn(propertyName);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, AsmTools.replaceClassName(InnerDelegateInvocation.class), propertyMethod.getName(), AsmTools.toAsmDesc(propertyMethod), false);
                AsmTools.codeBuilder_Cast(mv, asmType, null);
                mv.visitInsn(AsmTools.getReturn(asmType));
                mv.visitLabel(eLabel);
                mv.visitLocalVariable("this", AsmTools.toAsmType(InnerDelegateInvocation.class), null, sLabel, eLabel, 0);
                mv.visitMaxs(3, 1);
                mv.visitEnd();
            }
            if (propertyInfo.rwType == ReadWriteType.ReadWrite) {
                Label sLabel = new Label();
                Label eLabel = new Label();
                String methodName = "set" + StringUtils.firstCharToUpperCase(propertyName);
                String methodDescriptor = "(" + asmType + ")V";
                Method propertyMethod = InnerDelegateInvocation.class.getMethod("setProperty", String.class, Object.class, String.class, Object.class);
                //
                MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDescriptor, null, new String[] { "java/lang/Throwable" });
                mv.visitCode();
                mv.visitLabel(sLabel);
                mv.visitLdcInsn(getClassName() + "_set_" + propertyName);
                mv.visitVarInsn(ALOAD, 0);// this
                mv.visitLdcInsn(propertyName);
                mv.visitVarInsn(AsmTools.getLoad(asmType), 1);
                AsmTools.codeBuilder_valueOf(mv, AsmTools.toAsmType(propertyInfo.propertyType));
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, AsmTools.replaceClassName(InnerDelegateInvocation.class), propertyMethod.getName(), AsmTools.toAsmDesc(propertyMethod), false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitLabel(eLabel);
                mv.visitLocalVariable("this", AsmTools.toAsmType(InnerDelegateInvocation.class), null, sLabel, eLabel, 0);
                mv.visitLocalVariable(propertyName, AsmTools.toAsmType(Object.class), null, sLabel, eLabel, 1);
                mv.visitMaxs(4, 2);
                mv.visitEnd();
            }
        }
        //
        classWriter.visitEnd();
        this.classBytes = classWriter.toByteArray();
        if (this.classWritePath != null) {
            File outFile = new File(this.classWritePath, thisClassName + ".class");
            outFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(outFile, false);) {
                fos.write(this.classBytes);
                fos.flush();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        this.parentLoader.addClassConfig(this);
        this.classType = this.parentLoader.findClass(getClassName());
        return (Class<? extends T>) this.classType;
    }

    /** 把生成的字节码数据写入到这个目录中。 */
    public void classWriteToPath(File classWritePath) throws IOException {
        Objects.requireNonNull(classWritePath);
        if (!classWritePath.canWrite()) {
            throw new IOException("path cannot be written.");
        }
        this.classWritePath = classWritePath;
    }

    public File getClassWriteFile() {
        if (this.classWritePath == null) {
            return null;
        }
        return new File(this.classWritePath, AsmTools.replaceClassName(this.getClassName()) + ".class");
    }
}
