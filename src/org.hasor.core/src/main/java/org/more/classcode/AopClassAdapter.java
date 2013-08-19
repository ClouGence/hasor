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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.asm.ClassAdapter;
import org.more.asm.ClassVisitor;
import org.more.asm.FieldVisitor;
import org.more.asm.Label;
import org.more.asm.MethodVisitor;
import org.more.asm.Opcodes;
/**
 *该类的作用是在生成的类中加入aop的支持。
 * @version 2010-9-2
 * @author 赵永春 (zyc@hasor.net)
 */
class AopClassAdapter extends ClassAdapter implements Opcodes {
    private ClassBuilder        classBuilder        = null;
    private String              asmClassName        = null;
    //
    /**生成的Aop方法前缀*/
    public final static String  AopMethodPrefix     = "$aopFun";
    /**生成的字段名*/
    public final static String  AopMethodArrayName  = "$aopMethods";
    private final static String AopMethodType       = EngineToos.toAsmType(org.more.classcode.Method.class);
    private final static String AopMethodArrayType  = EngineToos.toAsmType(org.more.classcode.Method[].class);
    /**生成的字段名*/
    public final static String  AopFilterChainName  = "$aopFilterChain";
    /**具有aop特性的方法特定描述*/
    private ArrayList<String>   renderAopMethodList = new ArrayList<String>();
    //==================================================================================Constructor
    public AopClassAdapter(ClassVisitor visitor, ClassBuilder classBuilder) {
        super(visitor);
        this.classBuilder = classBuilder;
    }
    /**获取具有aop特性的方法集合。*/
    public ArrayList<String> getRenderAopMethodList() {
        return this.renderAopMethodList;
    }
    /**asm.visit，用于保存类名。*/
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.asmClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
    /**asm.visitMethod，遇到一个方法。*/
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        ClassEngine ce = this.classBuilder.getClassEngine();
        AopStrategy aopStrategy = ce.getAopStrategy();//获取Aop策略对象。
        //
        //1.准备输出方法数据，该方法的主要目的是从desc中拆分出参数表和返回值。
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        asmReturns = (asmReturns.charAt(0) == 'L') ? asmReturns.substring(1, asmReturns.length() - 1) : asmReturns;
        //
        //2.忽略构造方法，aop包装不会考虑构造方法。
        if (name.equals("<init>") == true)
            return super.visitMethod(access, name, desc, signature, exceptions);
        //
        //3.执行方法忽略策略，根据aop策略对象来决定忽略的方法列表。
        Class<?> superClass = ce.getSuperClass();
        Class<?>[] paramTypes = EngineToos.toJavaType(asmParams, ce.getRootClassLoader());
        Method method = EngineToos.findMethod(superClass, name, paramTypes);
        if (name.contains("$") == true)
            return super.visitMethod(access, name, desc, signature, exceptions);//忽略方法
        if (method != null)
            if (aopStrategy.isIgnore(superClass, method) == true)
                return super.visitMethod(access, name, desc, signature, exceptions);//忽略方法
        //
        //4.输出Aop代理方法。
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        mv.visitCode();
        String aopMethod = AopMethodPrefix + name + desc;//合成新方法的完整描述
        this.renderAopMethodList.add(aopMethod);
        int index = this.renderAopMethodList.indexOf(aopMethod);//确定新方法的输出索引用于输出新方法。
        this.visitAOPMethod(index, mv, name, desc);//输出新方法。
        mv.visitEnd();
        //
        //5.更改名称输出老方法
        String newMethodName = AopMethodPrefix + name;
        return super.visitMethod(access, newMethodName, desc, signature, exceptions);
    }
    /**asm.visitEnd，输出aop需要的特定属性。*/
    public void visitEnd() {
        //输出FilterChain的数组，是进入Aop的过滤器链。
        this.putSimpleProperty(AopFilterChainName, AopFilterChain_Start[].class);
        //输出Method的数组，Method保存的是Aop方法。
        this.putSimpleProperty(AopMethodArrayName, org.more.classcode.Method[].class);
        super.visitEnd();
    }
    /**输出简单属性，visitEnd方法调用，用于输出某一个属性的set方法和其字段。*/
    private void putSimpleProperty(String propertyName, Class<?> propertyType) {
        String asmFieldType = EngineToos.toAsmType(propertyType);
        FieldVisitor fv = super.visitField(ACC_PRIVATE, propertyName, asmFieldType, null, null);
        fv.visitEnd();
        MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "set" + EngineToos.toUpperCase(propertyName), "(" + asmFieldType + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);//装载this
        mv.visitVarInsn(ALOAD, 1);//装载参数
        mv.visitFieldInsn(PUTFIELD, this.asmClassName, propertyName, asmFieldType);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
    public void visitAOPMethod__(final int index, final MethodVisitor mv, final String originalMethodName, final String desc) {
        //
        //1.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int paramCount = asmParams.length;
        int localVarSize = paramCount + 1;//方法变量表大小
        int maxStackSize = 0;//方法最大堆栈大小
        //
        //2.输出数据
        //  mv = cw.visitMethod(ACC_PUBLIC, "getP_long", "(IZLjava/lang/Object;IZLjava/lang/Object;)J", null, null);
        //  mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(27, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, this.asmClassName, AopFilterChainName, EngineToos.toAsmType(AopFilterChain_Start[].class));
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(28, l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, this.asmClassName, AopMethodPrefix + originalMethodName, desc);
        mv.visitInsn(LRETURN);
        mv.visitLabel(l1);
        mv.visitLineNumber(29, l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitIntInsn(BIPUSH, 6);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_5);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitInsn(AASTORE);
        mv.visitVarInsn(ASTORE, 7);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(30, l3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, this.asmClassName, AopFilterChainName, EngineToos.toAsmType(AopFilterChain_Start[].class));
        mv.visitInsn(ICONST_0);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, this.asmClassName, AopMethodArrayName, AopMethodArrayType);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/more/core/classcode/AopFilterChain_Start", "doInvokeFilter", "(Ljava/lang/Object;Lorg/more/core/classcode/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
        mv.visitInsn(LRETURN);
        mv.visitMaxs(4, 8);
        mv.visitEnd();
    }
    /**实现AOP方法的输出，其中指令详见ASM3.2的{@link Opcodes}接口定义。 */
    public void visitAOPMethod(final int index, final MethodVisitor mv, final String originalMethodName, final String desc) {//, final Method method) {
        //
        //1.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int paramCount = asmParams.length;
        int localVarSize = paramCount + 1;//方法变量表大小
        int maxStackSize = 0;//方法最大堆栈大小
        //
        //2.输出数据
        mv.visitVarInsn(ALOAD, 0);//装载this
        mv.visitFieldInsn(GETFIELD, this.asmClassName, AopFilterChainName, EngineToos.toAsmType(AopFilterChain_Start[].class));
        Label ifTag = new Label();
        mv.visitJumpInsn(IFNONNULL, ifTag);
        //return this.$method_passObject(param);
        mv.visitVarInsn(ALOAD, 0);
        for (int i = 0; i < paramCount; i++)
            mv.visitVarInsn(ALOAD, i + 1);//装载参数
        mv.visitMethodInsn(INVOKEVIRTUAL, this.asmClassName, AopMethodPrefix + originalMethodName, desc);
        mv.visitInsn(EngineToos.getReturn(asmReturns));
        mv.visitLabel(ifTag);
        //else
        mv.visitVarInsn(ALOAD, 0);//装载this
        mv.visitFieldInsn(GETFIELD, this.asmClassName, AopFilterChainName, EngineToos.toAsmType(AopFilterChain_Start[].class));
        mv.visitIntInsn(BIPUSH, index);
        mv.visitInsn(AALOAD);//装载数组的第index个元素
        //param 1 this
        mv.visitVarInsn(ALOAD, 0);
        //param 2 $aopMethod[6]
        mv.visitVarInsn(ALOAD, 0);//
        mv.visitFieldInsn(GETFIELD, this.asmClassName, AopMethodArrayName, AopMethodArrayType);
        mv.visitIntInsn(BIPUSH, index);
        mv.visitInsn(AALOAD);
        //param 3 new Object[] { param }
        mv.visitIntInsn(BIPUSH, paramCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            if (asmParams[i].equals("B") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            } else if (asmParams[i].equals("S") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            } else if (asmParams[i].equals("I") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else if (asmParams[i].equals("J") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            } else if (asmParams[i].equals("F") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            } else if (asmParams[i].equals("D") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            } else if (asmParams[i].equals("C") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            } else if (asmParams[i].equals("Z") == true) {
                mv.visitVarInsn(EngineToos.getLoad(asmType), i + 1);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            } else
                mv.visitVarInsn(ALOAD, i + 1);
            mv.visitInsn(AASTORE);
        }
        //chain.doInvokeFilter(this, $aopMethod[6], new Object[] { param })
        String aop_desc = "(Ljava/lang/Object;" + AopMethodType + "[Ljava/lang/Object;)Ljava/lang/Object;";
        mv.visitMethodInsn(INVOKEVIRTUAL, EngineToos.replaceClassName(AopFilterChain_Start.class.getName()), "doInvokeFilter", aop_desc);
        //return (String)a;
        if (asmReturns.equals("B") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
        } else if (asmReturns.equals("S") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
        } else if (asmReturns.equals("I") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
        } else if (asmReturns.equals("J") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
        } else if (asmReturns.equals("F") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
        } else if (asmReturns.equals("D") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
        } else if (asmReturns.equals("C") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
        } else if (asmReturns.equals("Z") == true) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
        } else if (asmReturns.equals("V") == true) {
            //void
        } else {
            String asmReturnsType = (asmReturns.charAt(0) == 'L') ? asmReturns.substring(1, asmReturns.length() - 1) : asmReturns;
            mv.visitTypeInsn(CHECKCAST, asmReturnsType);
        }
        mv.visitInsn(EngineToos.getReturn(asmReturns));
        /* 输出堆栈列表 */
        mv.visitMaxs(maxStackSize, localVarSize + 1);
    }
}