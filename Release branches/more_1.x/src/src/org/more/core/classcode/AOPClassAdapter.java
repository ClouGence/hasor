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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.FieldVisitor;
import org.more.core.asm.Label;
import org.more.core.asm.MethodVisitor;
import org.more.core.asm.Opcodes;
import org.more.core.asm.Type;
/**
 * 完成AOP代理的字节码改写对象。
 * Date : 2009-10-30
 * @author 赵永春
 */
class AOPClassAdapter extends ClassAdapter implements Opcodes {
    //========================================================================================Field
    /** 当前类类名 */
    private String   thisClassByASM = null;
    private String[] ignoreMethod   = new String[] { "set" + ClassEngine.ObjectDelegateMapName, "set" + ClassEngine.AOPFilterChainName };
    //==================================================================================Constructor
    public AOPClassAdapter(ClassVisitor cv) {
        super(cv);
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.thisClassByASM = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //1.不处理构造方法
        if (name.equals("<init>") == true)
            return super.visitMethod(access, name, desc, signature, exceptions);
        //2.忽略More注入方法
        for (String n : this.ignoreMethod)
            if (n.equals(name) == true)
                return super.visitMethod(access, name, desc, signature, exceptions);
        //3.输出新方法
        String newMethodName = ClassEngine.AOPMethodNamePrefix + name;
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        this.visitAOPMethod(mv, name, desc);
        //4.更改名称输出老方法
        return super.visitMethod(ACC_PUBLIC, newMethodName, desc, signature, exceptions);
    }
    @Override
    public void visitEnd() {
        {
            //1.输出代理字段
            FieldVisitor field = super.visitField(ACC_PRIVATE, ClassEngine.AOPFilterChainName, "Lorg/more/core/classcode/ImplAOPFilterChain;", null, null);
            field.visitEnd();
            //2.输出代理字段的注入方法,方法名仅仅是代理字段的名称前面加上set代理字段首字母不需要大写。
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "set" + ClassEngine.AOPFilterChainName, "(Lorg/more/core/classcode/ImplAOPFilterChain;)V", null, null);
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitVarInsn(ALOAD, 1);//装载参数 
            mv.visitFieldInsn(PUTFIELD, this.thisClassByASM, ClassEngine.AOPFilterChainName, "Lorg/more/core/classcode/ImplAOPFilterChain;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        super.visitEnd();
    }
    /** 实现AOP方法 */
    public void visitAOPMethod(final MethodVisitor mv, String name, String desc) {//, final Method method) {
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        asmReturns = (asmReturns.charAt(0) == 'L') ? asmReturns.substring(1, asmReturns.length() - 1) : asmReturns;
        int paramCount = asmParams.length;
        int localVarSize = paramCount;//方法变量表大小
        int maxStackSize = 0;//方法最大堆栈大小
        //-----------------------------------------------------------------------------------------------------------------------
        mv.visitCode();
        Label try_begin = new Label();
        Label try_end = new Label();
        Label try_catch = new Label();
        mv.visitTryCatchBlock(try_begin, try_end, try_catch, "java/lang/Throwable");
        mv.visitLabel(try_begin);
        //-----------------------------------------------------------------------------------------------------------------------
        mv.visitIntInsn(BIPUSH, paramCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        for (int i = 0; i < paramCount; i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            if (asmParams[i].equals("B") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("S") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("I") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("J") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("F") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("D") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("C") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            else if (asmParams[i].equals("Z") == true)
                mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            else
                mv.visitLdcInsn(Type.getObjectType(EngineToos.toClassType(asmParams[i])));
            mv.visitInsn(AASTORE);
            maxStackSize = (maxStackSize < 5 + i) ? 5 + i : maxStackSize;
        }
        mv.visitVarInsn(ASTORE, paramCount + 1);
        localVarSize++;
        //Class[] paramTypes = new Class[]{...}----------------------------------------------------------------------------------
        mv.visitTypeInsn(NEW, "org/more/core/classcode/AOPMethods");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, this.thisClassByASM, "getClass", "()Ljava/lang/Class;");
        mv.visitLdcInsn(ClassEngine.AOPMethodNamePrefix + name);
        mv.visitVarInsn(ALOAD, paramCount + 1);
        mv.visitMethodInsn(INVOKESTATIC, "org/more/core/classcode/EngineToos", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
        //m1 EngineToos.getMethod(getClass(),"xxx", paramTypes);-----------------------------------------------------------------
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, this.thisClassByASM, "getClass", "()Ljava/lang/Class;");
        mv.visitLdcInsn(name);
        mv.visitVarInsn(ALOAD, paramCount + 1);
        mv.visitMethodInsn(INVOKESTATIC, "org/more/core/classcode/EngineToos", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
        //m2 EngineToos.getMethod(getClass(),"xxx", paramTypes);-----------------------------------------------------------------
        mv.visitMethodInsn(INVOKESPECIAL, "org/more/core/classcode/AOPMethods", "<init>", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V");
        mv.visitVarInsn(ASTORE, paramCount + 2);
        localVarSize++;
        //AOPMethods aop = new AOPMethods(m1,m2);--------------------------------------------------------------------------------
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, this.thisClassByASM, ClassEngine.AOPFilterChainName, "Lorg/more/core/classcode/ImplAOPFilterChain;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, paramCount + 2);
        mv.visitIntInsn(BIPUSH, paramCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            if (asmParams[i].equals("B")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            } else if (asmParams[i].equals("S")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            } else if (asmParams[i].equals("I")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else if (asmParams[i].equals("J")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            } else if (asmParams[i].equals("F")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            } else if (asmParams[i].equals("D")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            } else if (asmParams[i].equals("C")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            } else if (asmParams[i].equals("Z")) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            } else
                mv.visitVarInsn(ALOAD, i + 1);
            mv.visitInsn(AASTORE);
            maxStackSize = (maxStackSize < 8 + i) ? 8 + i : maxStackSize;
        }
        String desc2 = "Ljava/lang/Object;Lorg/more/core/classcode/AOPMethods;[Ljava/lang/Object;";
        mv.visitMethodInsn(INVOKEINTERFACE, "org/more/core/classcode/AOPFilterChain", "doInvokeFilter", "(" + desc2 + ")Ljava/lang/Object;");
        mv.visitVarInsn(ASTORE, paramCount + 3);
        localVarSize++;
        //obj = AOPFilterChainName.doInvokeFilter(this,thisMethod, new Object[] { methodCode });---------------------------------
        mv.visitVarInsn(ALOAD, paramCount + 3);
        if (asmReturns.equals("B") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            mv.visitInsn(EngineToos.getReturn("B"));
        } else if (asmReturns.equals("S") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            mv.visitInsn(EngineToos.getReturn("S"));
        } else if (asmReturns.equals("I") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            mv.visitInsn(EngineToos.getReturn("I"));
        } else if (asmReturns.equals("J") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            mv.visitInsn(EngineToos.getReturn("J"));
        } else if (asmReturns.equals("F") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            mv.visitInsn(EngineToos.getReturn("F"));
        } else if (asmReturns.equals("D") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
            mv.visitInsn(EngineToos.getReturn("D"));
        } else if (asmReturns.equals("C") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            mv.visitInsn(EngineToos.getReturn("C"));
        } else if (asmReturns.equals("Z") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            mv.visitInsn(EngineToos.getReturn("Z"));
        } else if (asmReturns.equals("V") == true) {
            mv.visitInsn(RETURN);
        } else {
            mv.visitTypeInsn(CHECKCAST, asmReturns);
            mv.visitInsn(ARETURN);
        }
        mv.visitLabel(try_end);
        //return obj-------------------------------------------------------------------------------------------------------------
        mv.visitLabel(try_catch);
        mv.visitVarInsn(ASTORE, paramCount + 4);
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, paramCount + 4);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V");
        mv.visitInsn(ATHROW);
        /* 输出堆栈列表 */
        mv.visitMaxs(maxStackSize, localVarSize + 1);
        mv.visitEnd();
    }
}