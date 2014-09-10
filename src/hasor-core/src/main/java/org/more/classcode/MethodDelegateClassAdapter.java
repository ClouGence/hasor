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
import java.util.ArrayList;
import org.more.asm.ClassVisitor;
import org.more.asm.Opcodes;
/**
 * 该类负责输出代理方法。
 * @version 2010-8-12
 * @author 赵永春 (zyc@hasor.net)
 */
class MethodDelegateClassAdapter extends ClassVisitor implements Opcodes {
    private String            superClassName = null; //父类类名
    private String            thisClassName  = null; //当前类名
    private ClassConfig       classConfig    = null;
    private ArrayList<String> delegateList   = null;
    //
    public MethodDelegateClassAdapter(final ClassVisitor cv, final ClassConfig config) {
        super(Opcodes.ASM4, cv);
        this.classConfig = config;
        this.delegateList = new ArrayList<String>();
    }
    //
    public void visit(final int version, final int access, String name, final String signature, String superName, String[] interfaces) {
        this.superClassName = superName;
        this.thisClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
    //
    public void visitEnd() {
        // TODO Auto-generated method stub
        super.visitEnd();
    }
    //
    //输出简单属性
    //    private void putSimpleProperty(final String propertyName, final Class<?> propertyType, final boolean isWriteOnly, final boolean isReadOnly) {
    //        String asmFieldType = EngineToos.toAsmType(propertyType);
    //        FieldVisitor fv = super.visitField(Opcodes.ACC_PRIVATE, propertyName, asmFieldType, null, null);
    //        fv.visitEnd();
    //        if (isWriteOnly == false) {
    //            this.putGetMethod(propertyName, asmFieldType);//get
    //        }
    //        if (isReadOnly == false) {
    //            this.putSetMethod(propertyName, asmFieldType);//set
    //        }
    //    }
    //
    //输出委托属性
    //    private void putDelegateProperty(final int index, final String propertyName, final PropertyDelegate<?> fieldDelegate, final boolean isWriteOnly, final boolean isReadOnly) {
    //        String asmDelegateType2 = EngineToos.replaceClassName(PropertyDelegate.class.getName());
    //        //
    //        Class<?> javaFieldType = fieldDelegate.getType();
    //        String asmFieldType = EngineToos.toAsmType(javaFieldType);
    //        String asmFieldType2 = EngineToos.replaceClassName(javaFieldType.getName());
    //        if (isWriteOnly == false) {
    //            //get
    //            MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "get" + EngineToos.toUpperCase(propertyName), "()" + asmFieldType, null, null);
    //            mv.visitCode();
    //            mv.visitVarInsn(Opcodes.ALOAD, 0);//装载this
    //            mv.visitFieldInsn(Opcodes.GETFIELD, this.asmClassName, MethodDelegateClassAdapter.PropertyArrayName, MethodDelegateClassAdapter.PropertyDelegateArrayType);
    //            mv.visitIntInsn(Opcodes.BIPUSH, index);
    //            mv.visitInsn(Opcodes.AALOAD);
    //            mv.visitVarInsn(Opcodes.ALOAD, 0);//装载this
    //            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, asmDelegateType2, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, asmFieldType2);
    //            mv.visitInsn(EngineToos.getReturn(asmFieldType));
    //            mv.visitMaxs(2, 1);
    //            mv.visitEnd();
    //        }
    //        if (isReadOnly == false) {
    //            //set
    //            MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "set" + EngineToos.toUpperCase(propertyName), "(" + asmFieldType + ")V", null, null);
    //            mv.visitCode();
    //            mv.visitVarInsn(Opcodes.ALOAD, 0);//装载this
    //            mv.visitFieldInsn(Opcodes.GETFIELD, this.asmClassName, MethodDelegateClassAdapter.PropertyArrayName, MethodDelegateClassAdapter.PropertyDelegateArrayType);
    //            mv.visitIntInsn(Opcodes.BIPUSH, index);
    //            mv.visitInsn(Opcodes.AALOAD);
    //            mv.visitVarInsn(Opcodes.ALOAD, 0);//装载this
    //            mv.visitVarInsn(Opcodes.ALOAD, 1);//装载param1
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, asmFieldType2);
    //            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, asmDelegateType2, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    //            mv.visitInsn(Opcodes.RETURN);
    //            mv.visitMaxs(1, 1);
    //            mv.visitEnd();
    //        }
    //    }
    //
    //实现接口附加
    //    private void visitInterfaceMethod(final int classIndex, final int methodIndex, final MethodDelegateClassAdapter adapter, final MethodVisitor mv, final Class<?> type, final String name, final String desc) {
    //        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
    //        Matcher m = p.matcher(desc);
    //        m.find();
    //        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
    //        String asmReturns = EngineToos.asmTypeToType(m.group(2));
    //        int paramCount = asmParams.length;
    //        int localVarSize = paramCount;//方法变量表大小
    //        int maxStackSize = 0;//方法最大堆栈大小
    //        //-----------------------------------------------------------------------------------------------------------------------
    //        mv.visitCode();
    //        mv.visitVarInsn(Opcodes.ALOAD, 0);
    //        mv.visitFieldInsn(Opcodes.GETFIELD, this.asmClassName, MethodDelegateClassAdapter.DelegateArrayName, MethodDelegateClassAdapter.DelegateArrayType);
    //        mv.visitIntInsn(Opcodes.BIPUSH, classIndex);
    //        mv.visitInsn(Opcodes.AALOAD);
    //        //参数1
    //        mv.visitVarInsn(Opcodes.ALOAD, 0);
    //        mv.visitFieldInsn(Opcodes.GETFIELD, this.asmClassName, MethodDelegateClassAdapter.DelegateMethodArrayName, MethodDelegateClassAdapter.DelegateMethodArrayType);
    //        mv.visitIntInsn(Opcodes.BIPUSH, methodIndex);
    //        mv.visitInsn(Opcodes.AALOAD);
    //        //参数2
    //        mv.visitVarInsn(Opcodes.ALOAD, 0);
    //        //参数3
    //        mv.visitIntInsn(Opcodes.BIPUSH, paramCount);
    //        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
    //        for (int i = 0; i < paramCount; i++) {
    //            mv.visitInsn(Opcodes.DUP);
    //            mv.visitIntInsn(Opcodes.BIPUSH, i);
    //            String asmType = asmParams[i];
    //            if (asmParams[i].equals("B")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
    //            } else if (asmParams[i].equals("S")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
    //            } else if (asmParams[i].equals("I")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
    //            } else if (asmParams[i].equals("J")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
    //            } else if (asmParams[i].equals("F")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
    //            } else if (asmParams[i].equals("D")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
    //            } else if (asmParams[i].equals("C")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
    //            } else if (asmParams[i].equals("Z")) {
    //                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
    //                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
    //            } else {
    //                mv.visitVarInsn(Opcodes.ALOAD, i + 1);
    //            }
    //            mv.visitInsn(Opcodes.AASTORE);
    //            maxStackSize = maxStackSize < 5 + i ? 5 + i : maxStackSize;
    //        }
    //        //调用
    //        String delegateType2 = EngineToos.replaceClassName(MethodDelegate.class.getName());
    //        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, delegateType2, "invoke", "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
    //        //return
    //        if (asmReturns.equals("B") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
    //            mv.visitInsn(EngineToos.getReturn("B"));
    //        } else if (asmReturns.equals("S") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
    //            mv.visitInsn(EngineToos.getReturn("S"));
    //        } else if (asmReturns.equals("I") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
    //            mv.visitInsn(EngineToos.getReturn("I"));
    //        } else if (asmReturns.equals("J") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
    //            mv.visitInsn(EngineToos.getReturn("J"));
    //        } else if (asmReturns.equals("F") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
    //            mv.visitInsn(EngineToos.getReturn("F"));
    //        } else if (asmReturns.equals("D") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
    //            mv.visitInsn(EngineToos.getReturn("D"));
    //        } else if (asmReturns.equals("C") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
    //            mv.visitInsn(EngineToos.getReturn("C"));
    //        } else if (asmReturns.equals("Z") == true) {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
    //            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
    //            mv.visitInsn(EngineToos.getReturn("Z"));
    //        } else if (asmReturns.equals("V") == true) {
    //            mv.visitInsn(Opcodes.POP);
    //            mv.visitInsn(Opcodes.RETURN);
    //        } else {
    //            mv.visitTypeInsn(Opcodes.CHECKCAST, asmReturns);
    //            mv.visitInsn(Opcodes.ARETURN);
    //        }
    //        /* 输出堆栈列表 */
    //        mv.visitMaxs(maxStackSize, localVarSize + 1);
    //        mv.visitEnd();
    //    }
    //
    //公开某个字段的set方法
    //    private void putSetMethod(final String propertyName, final String asmFieldType) {
    //        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "set" + EngineToos.toUpperCase(propertyName), "(" + asmFieldType + ")V", null, null);
    //        mv.visitCode();
    //        mv.visitVarInsn(Opcodes.ALOAD, 0);//装载this
    //        mv.visitVarInsn(Opcodes.ALOAD, 1);//装载参数
    //        mv.visitFieldInsn(Opcodes.PUTFIELD, this.asmClassName, propertyName, asmFieldType);
    //        mv.visitInsn(Opcodes.RETURN);
    //        mv.visitMaxs(1, 1);
    //        mv.visitEnd();
    //    }
    //
    //公开某个字段的get方法
    //    private void putGetMethod(final String propertyName, final String asmFieldType) {
    //        //get
    //        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "get" + EngineToos.toUpperCase(propertyName), "()" + asmFieldType, null, null);
    //        mv.visitCode();
    //        mv.visitVarInsn(Opcodes.ALOAD, 0);//装载this
    //        mv.visitFieldInsn(Opcodes.GETFIELD, this.asmClassName, propertyName, asmFieldType);
    //        mv.visitInsn(EngineToos.getReturn(asmFieldType));
    //        mv.visitMaxs(1, 1);
    //        mv.visitEnd();
    //    }
}