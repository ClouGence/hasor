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
package org.more.classcode.delegate.faces;
import java.lang.reflect.Method;
import java.util.HashSet;
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
 * 该类负责输出代理方法。
 * @version 2010-8-12
 * @author 赵永春 (zyc@hasor.net)
 */
@SuppressWarnings("deprecation")
class MethodDelegateClassAdapter extends ClassVisitor implements Opcodes {
    private MethodClassConfig classConfig    = null;
    private String            superClassName = null;                 //父类类名
    private Set<String>       validMethod    = new HashSet<String>();
    //
    public MethodDelegateClassAdapter(final ClassVisitor cv, final MethodClassConfig classConfig) {
        super(Opcodes.ASM4, cv);
        this.classConfig = classConfig;
    }
    //
    /**用于更新类名。*/
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        //1.类名
        this.superClassName = name;
        String className = this.classConfig.getClassName();
        className = classConfig.getClassName().replace(".", "/");
        //2.确定接口
        Set<String> newFaces = new HashSet<String>();
        for (String faces : interfaces) {
            newFaces.add(faces);
        }
        InnerMethodDelegateDefine[] defineArrays = this.classConfig.getNewDelegateList();
        for (InnerMethodDelegateDefine define : defineArrays) {
            Class<?> faceType = define.getFaces();
            newFaces.add(ASMEngineToos.replaceClassName(faceType));
        }
        String[] finalInterfaces = newFaces.toArray(new String[newFaces.size()]);
        //
        super.visit(version, access, className, signature, name, finalInterfaces);
    }
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }
    //
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //1.采集方法
        String findDesc = name + desc.substring(0, desc.lastIndexOf(")") + 1);
        this.validMethod.add(findDesc);
        //2.忽略构造方法，aop包装不会考虑构造方法。
        if (name.equals("<init>") == true) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            mv.visitCode();
            this.visitConstruction(mv, name, desc);
            mv.visitEnd();
            return null;
        }
        return null;
    }
    //
    public void visitEnd() {
        InnerMethodDelegateDefine[] defineArrays = this.classConfig.getNewDelegateList();
        for (InnerMethodDelegateDefine define : defineArrays) {
            Class<?> faceType = define.getFaces();
            Method[] faceMethods = faceType.getMethods();
            for (Method tMethod : faceMethods) {
                String mName = tMethod.getName();
                String typeDesc = String.format("%s(%s)", mName, ASMEngineToos.toAsmType(tMethod.getParameterTypes()));
                // 
                if (this.validMethod.contains(typeDesc) == false) {
                    String desc = ASMEngineToos.toAsmDesc(tMethod);
                    MethodVisitor mv = super.visitMethod(ACC_PUBLIC, mName, desc, null, null);
                    mv.visitCode();
                    this.buildInterfaceMethod(mv, mName, desc, faceType);
                    mv.visitEnd();
                }
                //
            }
            //
        }
        super.visitEnd();
    }
    //实现接口附加
    private void buildInterfaceMethod(MethodVisitor mv, String name, String desc, Class<?> faceType) {
        //例子代码
        //    public int getNames(int abc, Object abcc) {
        //        try {
        //            Class<?>[] arrayOfClass = new Class[] { int.class, Object.class };
        //            Object[] arrayOfObject = new Object[] { abc, abcc };
        //            //
        //            Method localMethod = List.class.getMethod("getNames", arrayOfClass);
        //            ClassLoader localLoader = getClass().getClassLoader();
        //            //
        //            Object target = new InnerChainMethodDelegate("xxxx", localLoader).invoke(localMethod, this, arrayOfObject);
        //            return ((Integer) target).intValue();
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
        int maxStack = 4;//方法最大堆栈大小
        int maxLocals = paramCount + 5;//本地变量表大小
        //
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
        mv.visitTryCatchBlock(tryBegin, tryEnd, tryCatch, "java/lang/Throwable");
        {//try {
            mv.visitLabel(tryBegin);
            //Class<?>[] pTypes = new Class[] { int.class, Object.class, boolean.class, short.class };
            this.codeBuilder_2(mv, asmParams);
            mv.visitVarInsn(ASTORE, paramCount + 2);
            //Object[] pObjects = new Object[] { abc, abcc, abcc };
            this.codeBuilder_1(mv, asmParams);
            mv.visitVarInsn(ASTORE, paramCount + 3);
            //
            //List.class.getMethod("getNames", arrayOfClass);
            mv.visitLdcInsn(Type.getType(ASMEngineToos.toAsmType(faceType)));
            mv.visitLdcInsn(name);
            mv.visitVarInsn(ALOAD, paramCount + 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
            mv.visitVarInsn(ASTORE, paramCount + 4);
            //
            //ClassLoader localLoader = getClass().getClassLoader();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;");
            mv.visitVarInsn(ASTORE, paramCount + 5);
            //
            //Object target = new InnerChainMethodDelegate("xxxx", localLoader).invoke(localMethod, this, arrayOfObject);
            mv.visitTypeInsn(NEW, ASMEngineToos.replaceClassName(InnerChainMethodDelegate.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(this.classConfig.getClassName());
            mv.visitLdcInsn(faceType.getName());
            mv.visitVarInsn(ALOAD, paramCount + 5);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineToos.replaceClassName(InnerChainMethodDelegate.class), "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            mv.visitVarInsn(ALOAD, paramCount + 4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, paramCount + 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, ASMEngineToos.replaceClassName(InnerChainMethodDelegate.class), "invoke", "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitVarInsn(ASTORE, paramCount + 6);
            //
            //return
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
            mv.visitMaxs(maxStack, maxLocals);
        } // }
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
}