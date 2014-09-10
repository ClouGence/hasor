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
package org.more.classcode;
import org.more.asm.ClassVisitor;
import org.more.asm.Label;
import org.more.asm.MethodVisitor;
import org.more.asm.Opcodes;
import org.more.util.StringUtils;
/**
 * 该类负责输出代理属性。
 * @version : 2014年9月8日
 * @author 赵永春(zyc@hasor.net)
 */
class PropertyDelegateClassAdapter extends ClassVisitor implements Opcodes {
    private ClassConfig classConfig = null;
    //
    public PropertyDelegateClassAdapter(ClassVisitor cv, ClassConfig classConfig) {
        super(ASM4, cv);
        this.classConfig = classConfig;
    }
    public void visitEnd() {
        InnerPropertyDelegate[] newPropertyList = this.classConfig.getNewPropertyList();
        for (InnerPropertyDelegate property : newPropertyList) {
            MethodVisitor mv = null;
            String propertyName = property.propertyName();
            String $propertyName = StringUtils.firstCharToUpperCase(propertyName);
            String typeDesc = InnerEngineToos.toAsmType(property.getType());
            //
            if (property.isMarkRead()) {
                String desc = "()" + typeDesc;
                mv = super.visitMethod(ACC_PUBLIC, "get" + $propertyName, desc, null, null);
                mv.visitCode();
                this.buildGetMethod(mv, propertyName, typeDesc);
                mv.visitEnd();
            }
            if (property.isMarkWrite()) {
                String desc = "(" + typeDesc + ")V";
                mv = super.visitMethod(ACC_PUBLIC, "set" + $propertyName, desc, null, null);
                mv.visitCode();
                this.buildSetMethod(mv, propertyName, typeDesc);
                mv.visitEnd();
            }
            //
        }
        super.visitEnd();
    }
    //
    protected void buildGetMethod(MethodVisitor mv, String propertyName, String propertyAsmType) {
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
            mv.visitTypeInsn(NEW, InnerEngineToos.replaceClassName(InnerChainPropertyDelegate.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(this.classConfig.getClassName());
            mv.visitLdcInsn(propertyName);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, InnerEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            mv.visitMethodInsn(INVOKEVIRTUAL, InnerEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "get", "()Ljava/lang/Object;");
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
            mv.visitTypeInsn(NEW, InnerEngineToos.replaceClassName(InnerChainPropertyDelegate.class));
            mv.visitInsn(DUP);
            mv.visitLdcInsn(this.classConfig.getClassName());
            mv.visitLdcInsn(propertyName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, InnerEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            mv.visitVarInsn(InnerEngineToos.getLoad(propertyAsmType), 1);
            this.codeBuilder_2(mv, propertyAsmType);
            mv.visitMethodInsn(INVOKEVIRTUAL, InnerEngineToos.replaceClassName(InnerChainPropertyDelegate.class), "set", "(Ljava/lang/Object;)V");
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
    //
    //Code Builder “return ...”
    private void codeBuilder_1(MethodVisitor mv, String asmReturns) {
        if (asmReturns.equals("B") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            mv.visitInsn(InnerEngineToos.getReturn("B"));
        } else if (asmReturns.equals("S") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            mv.visitInsn(InnerEngineToos.getReturn("S"));
        } else if (asmReturns.equals("I") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            mv.visitInsn(InnerEngineToos.getReturn("I"));
        } else if (asmReturns.equals("J") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            mv.visitInsn(InnerEngineToos.getReturn("J"));
        } else if (asmReturns.equals("F") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            mv.visitInsn(InnerEngineToos.getReturn("F"));
        } else if (asmReturns.equals("D") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
            mv.visitInsn(InnerEngineToos.getReturn("D"));
        } else if (asmReturns.equals("C") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            mv.visitInsn(InnerEngineToos.getReturn("C"));
        } else if (asmReturns.equals("Z") == true) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            mv.visitInsn(InnerEngineToos.getReturn("Z"));
        } else if (asmReturns.equals("V") == true) {
            mv.visitInsn(Opcodes.POP);
            mv.visitInsn(Opcodes.RETURN);
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, InnerEngineToos.asmTypeToType(asmReturns));
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
}