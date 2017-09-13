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
package net.hasor.core.classcode.aop;
import net.hasor.core.classcode.ASMEngineTools;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.asm.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 该类的作用是在生成的类中加入aop的支持。
 * @version 2010-9-2
 * @author 赵永春 (zyc@hasor.net)
 */
@SuppressWarnings("deprecation")
class AopClassAdapter extends ClassVisitor implements Opcodes {
    public final static String         AopPrefix      = "$aopFun"; //生成的Aop方法前缀
    private             String         superClassName = null;      //父类类名
    private             String         thisClassName  = null;      //当前类名
    private             AopClassConfig classConfig    = null;      //Aop筛选器
    private             List<Method>   aopMethodMap   = null;      //符合Aop的方法
    private             Set<String>    validMethod    = null;      //代理类自身方法
    //
    public AopClassAdapter(final ClassVisitor visitor, AopClassConfig classConfig) {
        super(ASM4, visitor);
        this.classConfig = classConfig;
        this.thisClassName = classConfig.getClassName().replace(".", "/");
        this.aopMethodMap = new ArrayList<Method>(); //符合Aop的方法
        this.validMethod = new HashSet<String>(); //代理类自身方法
    }
    //
    /**asm.visit，用于保存类名。*/
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.superClassName = name;
        this.visitBegin();
        super.visit(version, access, this.thisClassName, signature, this.superClassName, interfaces);
    }
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }
    //
    private void visitBegin() {
        //1.采集符合Aop切面期望的方法。
        Method[] methodSet1 = this.classConfig.getSuperClass().getMethods();
        for (Method targetMethod : methodSet1) {
            String tmDesc = ASMEngineTools.toAsmFullDesc(targetMethod);
            int dataModifiers = targetMethod.getModifiers();
            if (/**/ASMEngineTools.checkIn(dataModifiers, Modifier.PRIVATE) || //
                    ASMEngineTools.checkIn(dataModifiers, Modifier.FINAL) || //
                    ASMEngineTools.checkIn(dataModifiers, Modifier.STATIC)) {
                continue;
            }
            //
            AopInterceptor[] aop = this.classConfig.findInterceptor(tmDesc);
            if (aop == null || aop.length == 0) {
                continue;
            }
            this.aopMethodMap.add(targetMethod);
        }
        //2.输出Aop代理方法
        for (Method targetMethod : this.aopMethodMap) {
            int access = targetMethod.getModifiers();
            String name = targetMethod.getName();
            String desc = ASMEngineTools.toAsmDesc(targetMethod);
            String signature = ASMEngineTools.toAsmSignature(targetMethod);
            Class<?>[] errors = targetMethod.getExceptionTypes();
            String[] exceptions = new String[errors.length];
            for (int i = 0; i < errors.length; i++) {
                exceptions[i] = ASMEngineTools.replaceClassName(errors[i]);
            }
            //
            if (ASMEngineTools.checkIn(access, Modifier.NATIVE)) {
                access = access - Modifier.NATIVE;
            }
            //
            MethodVisitor mv = this.visitMethod(access, name, desc, signature, exceptions);
            mv.visitCode();
            this.visitProxyMethod(mv, name, desc);//输出新方法。
            mv.visitEnd();
        }
        //3.输出“$aopFun”方法
        for (Method targetMethod : this.aopMethodMap) {
            int access = Modifier.PRIVATE;
            String name = targetMethod.getName();
            String desc = ASMEngineTools.toAsmDesc(targetMethod);
            String signature = ASMEngineTools.toAsmSignature(targetMethod);
            Class<?>[] errors = targetMethod.getExceptionTypes();
            String[] exceptions = new String[errors.length];
            for (int i = 0; i < errors.length; i++) {
                exceptions[i] = ASMEngineTools.replaceClassName(errors[i]);
            }
            //
            MethodVisitor mv = this.visitMethod(access, AopPrefix + name, desc, signature, exceptions);
            mv.visitCode();
            this.visitAOPMethod(mv, name, desc);//输出新方法。
            mv.visitEnd();
        }
        //4.采集类本身的方法。
        Method[] methodSet2 = this.classConfig.getSuperClass().getDeclaredMethods();
        for (Method targetMethod : methodSet2) {
            String selfMethodDesc = ASMEngineTools.toAsmFullDesc(targetMethod);
            this.validMethod.add(selfMethodDesc);
        }
    }
    /**处理构造方法*/
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if ("<clinit>".equals(name)) {
            return null;
        }
        //
        //1.准备输出方法数据，该方法的主要目的是从desc中拆分出参数表和返回值。
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String asmReturns = m.group(2);
        asmReturns = asmReturns.charAt(0) == 'L' ? asmReturns.substring(1, asmReturns.length() - 1) : asmReturns;
        //
        //2.忽略构造方法，aop包装不会考虑构造方法。
        if ("<init>".equals(name)) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            mv.visitCode();
            this.visitConstruction(mv, name, desc);
            mv.visitEnd();
            return null;
        }
        //2.兼容如果有其它ClassVisitor动态输出方法时不会影响到它。
        String testFullDesc = name + desc;
        if (!this.validMethod.contains(testFullDesc)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        return null;
    }
    //
    //Code Builder “new Object[] { abc, abcc, abcc };”
    private void codeBuilder_1(MethodVisitor mv, String[] asmParams) {
        int paramCount = asmParams.length;
        mv.visitIntInsn(Opcodes.BIPUSH, paramCount);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            if (asmParams[i].equals("B")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            } else if (asmParams[i].equals("S")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            } else if (asmParams[i].equals("I")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else if (asmParams[i].equals("J")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            } else if (asmParams[i].equals("F")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            } else if (asmParams[i].equals("D")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            } else if (asmParams[i].equals("C")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            } else if (asmParams[i].equals("Z")) {
                mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, i + 1);
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }
    //Code Builder “new Class[] { int.class, Object.class, boolean.class, short.class };”
    private void codeBuilder_2(MethodVisitor mv, String[] asmParams) {
        int paramCount = asmParams.length;
        mv.visitIntInsn(Opcodes.BIPUSH, paramCount);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            if (asmParams[i].equals("B")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("S")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("I")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("J")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("F")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("D")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("C")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("Z")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else {
                mv.visitLdcInsn(Type.getType(asmType));//  Ljava/lang/Object;
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }
    //Code Builder “return ...”
    private void codeBuilder_3(MethodVisitor mv, String asmReturns) {
        if (asmReturns.equals("B")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            mv.visitInsn(ASMEngineTools.getReturn("B"));
        } else if (asmReturns.equals("S")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            mv.visitInsn(ASMEngineTools.getReturn("S"));
        } else if (asmReturns.equals("I")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            mv.visitInsn(ASMEngineTools.getReturn("I"));
        } else if (asmReturns.equals("J")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            mv.visitInsn(ASMEngineTools.getReturn("J"));
        } else if (asmReturns.equals("F")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            mv.visitInsn(ASMEngineTools.getReturn("F"));
        } else if (asmReturns.equals("D")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
            mv.visitInsn(ASMEngineTools.getReturn("D"));
        } else if (asmReturns.equals("C")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            mv.visitInsn(ASMEngineTools.getReturn("C"));
        } else if (asmReturns.equals("Z")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            mv.visitInsn(ASMEngineTools.getReturn("Z"));
        } else if (asmReturns.equals("V")) {
            mv.visitInsn(Opcodes.POP);
            mv.visitInsn(Opcodes.RETURN);
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, ASMEngineTools.asmTypeToType(asmReturns));
            mv.visitInsn(Opcodes.ARETURN);
        }
    }
    //
    private void visitProxyMethod(MethodVisitor mv, String name, String desc) {
        // 生成的例子代码：
        //    public int doCall(int abc, Object abcc) {
        //        Class<?>[] pTypes = new Class[] { int.class, Object.class, boolean.class, short.class };
        //        Object[] pObjects = new Object[] { abc, abcc, abcc };
        //        try {
        //            Method m = this.getClass().getMethod("doCall", pTypes);
        //            InnerChainAopInvocation chain = new InnerChainAopInvocation(pObjects, m, this);
        //            Object obj = new InnerAopInvocation(m, chain).proceed();
        //            return ((Integer) obj).intValue();
        //        } catch (Throwable e) {
        //            throw ExceptionUtils.toRuntimeException(e);
        //        }
        //    }
        //1.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = ASMEngineTools.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int paramCount = asmParams.length;
        int maxStack = 5;//方法最大堆栈大小
        int maxLocals = paramCount + 5;//本地变量表大小
        //
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
        Label lastReturn = new Label();
        mv.visitTryCatchBlock(tryBegin, tryEnd, tryCatch, "java/lang/Throwable");
        {//try {
            mv.visitLabel(tryBegin);
            this.codeBuilder_2(mv, asmParams);//Class<?>[] pTypes = new Class[] { int.class, Object.class, boolean.class, short.class };
            mv.visitVarInsn(ASTORE, paramCount + 2);
            this.codeBuilder_1(mv, asmParams);//Object[] pObjects = new Object[] { abc, abcc, abcc };
            mv.visitVarInsn(ASTORE, paramCount + 3);
            //
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, this.thisClassName, "getClass", "()Ljava/lang/Class;");
            mv.visitLdcInsn(name);
            mv.visitVarInsn(ALOAD, paramCount + 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
            mv.visitVarInsn(ASTORE, paramCount + 4);
            //
            mv.visitTypeInsn(NEW, ASMEngineTools.replaceClassName(InnerChainAopInvocation.class));
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, paramCount + 4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, paramCount + 3);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineTools.replaceClassName(InnerChainAopInvocation.class), "<init>", "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)V");
            mv.visitVarInsn(ASTORE, paramCount + 5);
            //
            mv.visitTypeInsn(NEW, ASMEngineTools.replaceClassName(InnerAopInvocation.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(name + desc);
            mv.visitVarInsn(ALOAD, paramCount + 4);
            mv.visitVarInsn(ALOAD, paramCount + 5);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineTools.replaceClassName(InnerAopInvocation.class), "<init>", "(Ljava/lang/String;Ljava/lang/reflect/Method;" + ASMEngineTools.toAsmType(AopInvocation.class) + ")V");//
            mv.visitMethodInsn(INVOKEVIRTUAL, ASMEngineTools.replaceClassName(InnerAopInvocation.class), "proceed", "()Ljava/lang/Object;");
            mv.visitVarInsn(ASTORE, paramCount + 6);
            mv.visitVarInsn(ALOAD, paramCount + 6);
            mv.visitLabel(tryEnd);
            this.codeBuilder_3(mv, asmReturns);
        }
        {//} catch (Exception e) {
            mv.visitLabel(tryCatch);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, ASMEngineTools.replaceClassName(ExceptionUtils.class), "toRuntimeException", "(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;", false);
            mv.visitInsn(ATHROW);
        } // }
        mv.visitMaxs(maxStack, maxLocals);
    }
    //
    private void visitConstruction(MethodVisitor mv, String name, String desc) {
        //1.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = ASMEngineTools.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        int paramCount = asmParams.length;
        //
        mv.visitVarInsn(ALOAD, 0);
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
        }
        mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, name, desc);
        mv.visitInsn(RETURN);
        mv.visitMaxs(paramCount + 1, paramCount + 1);
    }
    //
    private void visitAOPMethod(MethodVisitor mv, String name, String desc) {
        //1.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = ASMEngineTools.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int paramCount = asmParams.length;
        //
        mv.visitVarInsn(ALOAD, 0);
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitVarInsn(ASMEngineTools.getLoad(asmType), i + 1);
        }
        mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, name, desc);
        mv.visitInsn(ASMEngineTools.getReturn(asmReturns));
        mv.visitMaxs(paramCount + 1, paramCount + 1);
    }
}