/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode.delegate.property;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.asm.ClassVisitor;
import org.more.asm.Label;
import org.more.asm.MethodVisitor;
import org.more.asm.Opcodes;
import org.more.classcode.ASMEngineToos;
import org.more.util.StringUtils;
/**
 * 该类负责输出代理属性。
 * @version : 2014年9月8日
 * @author 赵永春(zyc@hasor.net)
 */
class PropertyDelegateClassAdapter extends ClassVisitor implements Opcodes {
    private PropertyClassConfig classConfig    = null;
    private String              superClassName = null;                 //父类类名
    private Set<String>         validMethod    = new HashSet<String>();
    //
    public PropertyDelegateClassAdapter(ClassVisitor cv, PropertyClassConfig classConfig) {
        super(ASM4, cv);
        this.classConfig = classConfig;
    }
    //
    /**用于更新类名。*/
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.superClassName = name;
        String className = this.classConfig.getClassName();
        className = classConfig.getClassName().replace(".", "/");
        super.visit(version, access, className, signature, name, interfaces);
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
    public void visitEnd() {
        InnerPropertyDelegateDefine[] newPropertyList = this.classConfig.getNewPropertyList();
        for (InnerPropertyDelegateDefine property : newPropertyList) {
            MethodVisitor mv = null;
            String propertyName = property.propertyName();
            String $propertyName = StringUtils.firstCharToUpperCase(propertyName);
            String typeDesc = ASMEngineToos.toAsmType(property.getType());
            //
            if (property.isMarkRead()) {
                String methodName = "get" + $propertyName;
                String desc = String.format("()%s", typeDesc);
                String testDesc = methodName + "()";
                //检测是否存在同名方法
                if (this.validMethod.contains(testDesc) == false) {
                    mv = super.visitMethod(ACC_PUBLIC, methodName, desc, null, null);
                    mv.visitCode();
                    this.buildGetMethod(mv, propertyName, typeDesc);
                    mv.visitEnd();
                }
            }
            if (property.isMarkWrite()) {
                String methodName = "set" + $propertyName;
                String desc = String.format("(%s)V", typeDesc);
                String testDesc = String.format("%s(%s)", methodName, typeDesc);;
                //检测是否存在同名方法
                if (this.validMethod.contains(testDesc) == false) {
                    mv = super.visitMethod(ACC_PUBLIC, methodName, desc, null, null);
                    mv.visitCode();
                    this.buildSetMethod(mv, propertyName, typeDesc);
                    mv.visitEnd();
                }
            }
            //
        }
        super.visitEnd();
    }
    //
    protected void buildGetMethod(MethodVisitor mv, String propertyName, String propertyAsmType) {
        // 例子代码：
        //      public int getName() {
        //      try {
        //          ClassLoader localClassLoader = super.getClass().getClassLoader();
        //          Object localObject = new InnerChainPropertyDelegate("net.test.simple.core._14_aop.Bean$Aop", "name", localClassLoader).get();
        //          return ((Integer) localObject).intValue();
        //      } catch (Throwable localThrowable) {
        //          if (localThrowable instanceof RuntimeException)
        //              throw ((RuntimeException) localThrowable);
        //          throw new RuntimeException(localThrowable);
        //      }
        //  }
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
        mv.visitTryCatchBlock(tryBegin, tryEnd, tryCatch, "java/lang/Throwable");
        {//try {
            mv.visitLabel(tryBegin);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;");
            mv.visitVarInsn(ASTORE, 1);
            mv.visitTypeInsn(NEW, ASMEngineToos.replaceClassName(InnerChainPropertyDelegate.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(this.classConfig.getClassName());
            mv.visitLdcInsn(propertyName);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            mv.visitMethodInsn(INVOKEVIRTUAL, ASMEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "get", "()Ljava/lang/Object;");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitLabel(tryEnd);
            this.codeBuilder_1(mv, propertyAsmType);
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
            mv.visitMaxs(4, 3);
        }// }
    }
    //
    protected void buildSetMethod(MethodVisitor mv, String propertyName, String propertyAsmType) {
        //例子代码
        //      public void setName(int paramInt) {
        //      try {
        //          ClassLoader localClassLoader = super.getClass().getClassLoader();
        //          new InnerChainPropertyDelegate("net.test.simple.core._14_aop.Bean$Aop", "name", localClassLoader).set(Integer.valueOf(paramInt));
        //          return;
        //      } catch (Throwable localThrowable) {
        //          if (localThrowable instanceof RuntimeException)
        //              throw ((RuntimeException) localThrowable);
        //          throw new RuntimeException(localThrowable);
        //      }
        //  }
        //
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
        mv.visitTryCatchBlock(tryBegin, tryEnd, tryCatch, "java/lang/Throwable");
        {//try {
            mv.visitLabel(tryBegin);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitTypeInsn(NEW, ASMEngineToos.replaceClassName(InnerChainPropertyDelegate.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(this.classConfig.getClassName());
            mv.visitLdcInsn(propertyName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, ASMEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            mv.visitVarInsn(ASMEngineToos.getLoad(propertyAsmType), 1);
            this.codeBuilder_2(mv, propertyAsmType);
            mv.visitMethodInsn(INVOKEVIRTUAL, ASMEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "set", "(Ljava/lang/Object;)V");
            mv.visitLabel(tryEnd);
            mv.visitInsn(RETURN);
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
            mv.visitMaxs(5, 3);
        }// }
    }
    //
    //Code Builder “return ...”
    private void codeBuilder_1(MethodVisitor mv, String asmReturns) {
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
    //Code Builder “new Object[] { abc, abcc, abcc };”
    private void codeBuilder_2(MethodVisitor mv, String asmType) {
        if (asmType.equals("B") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
        } else if (asmType.equals("S") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
        } else if (asmType.equals("I") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        } else if (asmType.equals("J") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
        } else if (asmType.equals("F") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
        } else if (asmType.equals("D") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
        } else if (asmType.equals("C") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
        } else if (asmType.equals("Z") == true) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        } else {
            //
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