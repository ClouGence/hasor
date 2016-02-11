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
package org.more.classcode.aop;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.asm.ClassVisitor;
import org.more.asm.FieldVisitor;
import org.more.asm.Label;
import org.more.asm.MethodVisitor;
import org.more.asm.Opcodes;
import org.more.asm.Type;
import org.more.classcode.ASMEngineToos;
/**
 * 该类的作用是在生成的类中加入aop的支持。
 * @version 2010-9-2
 * @author 赵永春 (zyc@hasor.net)
 */
class AopClassAdapter extends ClassVisitor implements Opcodes {
    public final static String AopPrefix      = "$aopFun"; //生成的Aop方法前缀
    private String             superClassName = null;      //父类类名
    private String             thisClassName  = null;      //当前类名
    private AopClassConfig     classConfig    = null;      //Aop筛选器
    private List<Method>       aopMethodMap   = null;      //符合Aop的方法
    private Set<String>        validMethod    = null;      //代理类自身方法
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
            String tmDesc = ASMEngineToos.toAsmFullDesc(targetMethod);
            int dataModifiers = targetMethod.getModifiers();
            if (/**/ASMEngineToos.checkIn(dataModifiers, Modifier.PRIVATE) == true || //
                    ASMEngineToos.checkIn(dataModifiers, Modifier.FINAL) == true) {
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
            String desc = ASMEngineToos.toAsmDesc(targetMethod);
            String signature = ASMEngineToos.toAsmSignature(targetMethod);
            Class<?>[] errors = targetMethod.getExceptionTypes();
            String[] exceptions = new String[errors.length];
            for (int i = 0; i < errors.length; i++) {
                exceptions[i] = ASMEngineToos.replaceClassName(errors[i]);
            }
            //
            if (ASMEngineToos.checkIn(access, Modifier.NATIVE)) {
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
            String desc = ASMEngineToos.toAsmDesc(targetMethod);
            String signature = ASMEngineToos.toAsmSignature(targetMethod);
            Class<?>[] errors = targetMethod.getExceptionTypes();
            String[] exceptions = new String[errors.length];
            for (int i = 0; i < errors.length; i++) {
                exceptions[i] = ASMEngineToos.replaceClassName(errors[i]);
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
            String selfMethodDesc = ASMEngineToos.toAsmFullDesc(targetMethod);
            this.validMethod.add(selfMethodDesc);
        }
    }
    /**处理构造方法*/
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        //1.准备输出方法数据，该方法的主要目的是从desc中拆分出参数表和返回值。
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String asmReturns = m.group(2);
        asmReturns = asmReturns.charAt(0) == 'L' ? asmReturns.substring(1, asmReturns.length() - 1) : asmReturns;
        //
        //2.忽略构造方法，aop包装不会考虑构造方法。
        if (name.equals("<init>") == true) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            mv.visitCode();
            this.visitConstruction(mv, name, desc);
            mv.visitEnd();
            return null;
        }
        //2.兼容如果有其它ClassVisitor动态输出方法时不会影响到它。
        String testFullDesc = name + desc;
        if (this.validMethod.contains(testFullDesc) == false) {
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
            if (asmParams[i].equals("B") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            } else if (asmParams[i].equals("S") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            } else if (asmParams[i].equals("I") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else if (asmParams[i].equals("J") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            } else if (asmParams[i].equals("F") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            } else if (asmParams[i].equals("D") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            } else if (asmParams[i].equals("C") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            } else if (asmParams[i].equals("Z") == true) {
                mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
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
            if (asmParams[i].equals("B") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("S") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("I") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("J") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("F") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("D") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("C") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("Z") == true) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else {
                mv.visitLdcInsn(Type.getType(asmType));//  Ljava/lang/Object;
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }
    //Code Builder “return ...”
    private void codeBuilder_3(MethodVisitor mv, String asmReturns) {
        if (asmReturns.equals("B") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            mv.visitInsn(ASMEngineToos.getReturn("B"));
        } else if (asmReturns.equals("S") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            mv.visitInsn(ASMEngineToos.getReturn("S"));
        } else if (asmReturns.equals("I") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            mv.visitInsn(ASMEngineToos.getReturn("I"));
        } else if (asmReturns.equals("J") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            mv.visitInsn(ASMEngineToos.getReturn("J"));
        } else if (asmReturns.equals("F") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            mv.visitInsn(ASMEngineToos.getReturn("F"));
        } else if (asmReturns.equals("D") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
            mv.visitInsn(ASMEngineToos.getReturn("D"));
        } else if (asmReturns.equals("C") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            mv.visitInsn(ASMEngineToos.getReturn("C"));
        } else if (asmReturns.equals("Z") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            mv.visitInsn(ASMEngineToos.getReturn("Z"));
        } else if (asmReturns.equals("V") == true) {
            mv.visitInsn(Opcodes.POP);
            mv.visitInsn(Opcodes.RETURN);
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, ASMEngineToos.asmTypeToType(asmReturns));
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
        //            throw new RuntimeException(e);
        //        }
        //    }
        //1.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = ASMEngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int paramCount = asmParams.length;
        int maxStack = 5;//方法最大堆栈大小
        int maxLocals = paramCount + 5;//本地变量表大小
        //
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
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
            mv.visitTypeInsn(NEW, ASMEngineToos.replaceClassName(InnerChainAopInvocation.class));
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, paramCount + 4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, paramCount + 3);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineToos.replaceClassName(InnerChainAopInvocation.class), "<init>", "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)V");
            mv.visitVarInsn(ASTORE, paramCount + 5);
            //
            mv.visitTypeInsn(NEW, ASMEngineToos.replaceClassName(InnerAopInvocation.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(name + desc);
            mv.visitVarInsn(ALOAD, paramCount + 4);
            mv.visitVarInsn(ALOAD, paramCount + 5);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineToos.replaceClassName(InnerAopInvocation.class), "<init>", "(Ljava/lang/String;Ljava/lang/reflect/Method;" + ASMEngineToos.toAsmType(AopInvocation.class) + ")V");//
            mv.visitMethodInsn(INVOKEVIRTUAL, ASMEngineToos.replaceClassName(InnerAopInvocation.class), "proceed", "()Ljava/lang/Object;");
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
            mv.visitTypeInsn(INSTANCEOF, "java/lang/RuntimeException");
            Label ifBlock = new Label();
            mv.visitJumpInsn(IFEQ, ifBlock);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitTypeInsn(CHECKCAST, "java/lang/RuntimeException");
            mv.visitInsn(ATHROW);
            mv.visitLabel(ifBlock);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V");
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
        String[] asmParams = ASMEngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        int paramCount = asmParams.length;
        //
        mv.visitVarInsn(ALOAD, 0);
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
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
        String[] asmParams = ASMEngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int paramCount = asmParams.length;
        //
        mv.visitVarInsn(ALOAD, 0);
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitVarInsn(ASMEngineToos.getLoad(asmType), i + 1);
        }
        mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, name, desc);
        mv.visitInsn(ASMEngineToos.getReturn(asmReturns));
        mv.visitMaxs(paramCount + 1, paramCount + 1);
    }
}