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
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.InvokeException;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassReader;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.ClassWriter;
import org.more.core.asm.FieldVisitor;
import org.more.core.asm.Label;
import org.more.core.asm.MethodVisitor;
import org.more.core.asm.Opcodes;
import org.more.core.asm.Type;
/**
 * 该类负责修改类的字节码附加接口实现方法。
 * 生成类过程
 * visit
 *   1.附加实现接口
 *   2.继承基类
 *   3.修改新类类名
 * visitMethod
 *   1.修改方法名为 _methodName
 *   2.输出代理方法
 *   3.增加本地方法集合
 * visitEnd
 *   1.扫描附加接口方法
 *   2.如果本地方法集合中存在该方法则忽略输出。如果该方法是被保护的或者私有的抛出异常
 *   3.输出代理方法调用
 * Date : 2009-10-22
 * @author 赵永春
 */
class BuilderClassAdapter extends ClassAdapter implements Opcodes {
    //========================================================================================Field
    /** xxxx */
    private ClassEngine                   engine          = null;
    /** 基类类名 */
    private String                        superClassByASM = null;
    /** 当前类类名 */
    private String                        thisClassByASM  = null;
    /** 生成的新类所要附加的接口实现 */
    private Map<Class<?>, MethodDelegate> implsMap        = null;
    /** 本类中已经存在的方法 */
    private ArrayList<String>             methodList      = new ArrayList<String>(0);
    private Class<?>                      superClassType;
    //==================================================================================Constructor
    /** ...... */
    public BuilderClassAdapter(ClassEngine engine, ClassVisitor cv, Class<?> superClass, Map<Class<?>, MethodDelegate> implsMap) {
        super(cv);
        this.engine = engine;
        this.superClassByASM = EngineToos.replaceClassName(superClass.getName());
        this.implsMap = implsMap;
        this.superClassType = superClass;
    }
    //==========================================================================================Job
    /** 附加接口实现 */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        //1.附加实现接口
        //2.继承基类
        //3.修改新类类名/
        ArrayList<String> al = new ArrayList<String>(0);
        //----------一、已经实现的接口
        Collections.addAll(al, interfaces);
        //----------二、附加接口实现
        for (Class<?> i : this.implsMap.keySet()) {
            String implType = EngineToos.replaceClassName(i.getName());
            if (al.contains(implType) == false)
                al.add(implType);
        }
        //----------三、转换List为Array
        String[] ins = new String[al.size()];
        al.toArray(ins);
        //----------四、继承基类、修改新类类名
        this.thisClassByASM = engine.getClassName().replace(".", "/");
        super.visit(version, access, this.thisClassByASM, signature, name, ins);
    }
    /** 调用父类方法 */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //1.如果工作在代理模式下则忽略所有私有方法和受保护的方法。
        if (this.engine.getMode() == ClassEngine.BuilderMode.Propxy)
            if (name.equals("<init>") || (access | ACC_PRIVATE) == access || (access | ACC_PROTECTED) == access)
                return null;
        //2.不处理私有方法保持原装
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ((access | ACC_PRIVATE) == access)
            return mv;//不处理私有方法的父类调用，维持原装
        //3.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        int maxLocals = 1;
        //3.输出方法
        mv.visitCode();
        if (this.engine.getMode() == ClassEngine.BuilderMode.Super) {
            //Super 如果是继承方式调用则使用super.invoke调用。
            mv.visitVarInsn(ALOAD, 0);
            for (int i = 0; i < asmParams.length; i++)
                mv.visitVarInsn(EngineToos.getLoad(asmParams[i]), i + 1);
            mv.visitMethodInsn(INVOKESPECIAL, this.superClassByASM, name, desc);
            maxLocals += asmParams.length;
        } else {
            //Propxy 如果是代理方式则使用this.$propxyObject.invoke。
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.thisClassByASM, ClassEngine.PropxyModeObjectName, "L" + this.superClassByASM + ";");
            for (int i = 0; i < asmParams.length; i++)
                mv.visitVarInsn(EngineToos.getLoad(asmParams[i]), i + 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, this.superClassByASM, name, desc);
            maxLocals += (asmParams.length + 1);
        }
        //4.处理方法调用的返回值return。
        if (asmReturns.equals("V") == true)
            mv.visitInsn(RETURN);
        else
            mv.visitInsn(EngineToos.getReturn(asmReturns));
        //5.结束方法输出，确定方法堆栈等信息。
        mv.visitMaxs(1, maxLocals);
        mv.visitEnd();
        //6.将已经处理的方法添加到本地方法表中并返回。
        methodList.add(name + desc);
        return null;
    }
    /** 输出接口附加方法 */
    @Override
    public void visitEnd() {
        try {
            {
                //1.输出代理字段
                FieldVisitor field = super.visitField(ACC_PRIVATE, ClassEngine.ObjectDelegateMapName, "Ljava/util/Hashtable;", null, null);
                field.visitEnd();
                //2.输出代理字段的注入方法,方法名仅仅是代理字段的名称前面加上set代理字段首字母不需要大写。
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "set" + ClassEngine.ObjectDelegateMapName, "(Ljava/util/Hashtable;)V", null, null);
                mv.visitVarInsn(ALOAD, 0);//装载this
                mv.visitVarInsn(ALOAD, 1);//装载参数 
                mv.visitFieldInsn(PUTFIELD, this.thisClassByASM, ClassEngine.ObjectDelegateMapName, "Ljava/util/Hashtable;");
                mv.visitInsn(RETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            {
                //3.如果工作在代理模式则输出代理对象相关方法。
                if (this.engine.getMode() == ClassEngine.BuilderMode.Propxy) {
                    //Super 如果是继承方式调用则输出代理字段。
                    String superClassNyASMType = "L" + this.superClassByASM + ";";
                    FieldVisitor propxy = super.visitField(ACC_PRIVATE, ClassEngine.PropxyModeObjectName, superClassNyASMType, null, null);
                    propxy.visitEnd();
                    MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "(" + superClassNyASMType + ")V", null, null);
                    mv.visitVarInsn(ALOAD, 0);//装载this
                    mv.visitMethodInsn(INVOKESPECIAL, this.superClassByASM, "<init>", "()V");
                    mv.visitVarInsn(ALOAD, 0);//装载this
                    mv.visitVarInsn(ALOAD, 1);//装载参数 
                    mv.visitFieldInsn(PUTFIELD, this.thisClassByASM, ClassEngine.PropxyModeObjectName, superClassNyASMType);
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                }
            }
            {
                //2.附加接口实现
                for (final Class<?> impl_type : this.implsMap.keySet()) {
                    InputStream inStream = EngineToos.getClassInputStream(impl_type);//获取输入流
                    ClassReader reader = new ClassReader(inStream);//创建ClassReader
                    final BuilderClassAdapter ca = this;
                    //扫描附加接口方法
                    reader.accept(new ClassAdapter(new ClassWriter(ClassWriter.COMPUTE_MAXS)) {
                        @Override
                        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                            if (ca.methodList.contains(name + desc) == true)
                                //如果本地方法集合中存在该方法则忽略输出。
                                return null;
                            ca.methodList.add(name + desc);
                            MethodVisitor mv = ca.cv.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
                            BuilderClassAdapter.visitInterfaceMethod(ca.engine, mv, impl_type, name, desc);//输出代理方法调用
                            return mv;
                        }
                    }, ClassReader.SKIP_DEBUG);
                }
            }
            //3.重写基类中的方法
            {
                for (java.lang.reflect.Method m : this.superClassType.getMethods()) {
                    String returnStr = EngineToos.toAsmType(m.getReturnType());
                    //
                    String desc = "(" + EngineToos.toAsmType(m.getParameterTypes()) + ")" + returnStr;
                    String fullDesc = m.getName() + desc;
                    //检测是否是已经存在的方法
                    if (this.methodList.contains(fullDesc) == true)
                        continue;
                    String[] exceptions = EngineToos.splitAsmType(EngineToos.toAsmType(m.getExceptionTypes()));
                    //处理类型
                    for (int i = 0; i < exceptions.length; i++)
                        exceptions[i] = exceptions[i].substring(1, exceptions[i].length() - 1);
                    exceptions = (exceptions.length == 0) ? null : exceptions;
                    //
                    int access = m.getModifiers();
                    if ((access | Modifier.FINAL) == access)
                        continue;//忽略常方法
                    else if ((access | Modifier.PUBLIC) == access)
                        this.visitMethod(ACC_PUBLIC, m.getName(), desc, null, exceptions);
                    else if ((access | Modifier.PROTECTED) == access)
                        this.visitMethod(ACC_PROTECTED, m.getName(), desc, null, exceptions);
                }
            }
            //4.继续
            super.visitEnd();
        } catch (Exception e) {
            throw new InvokeException("执行附加接口方法期间发生异常：" + e.getMessage(), e);
        }
    }
    /** 实现接口附加 */
    public static void visitInterfaceMethod(final ClassEngine engine, final MethodVisitor mv, Class<?> inplType, String name, String desc) {//, final Method method) {
        String replaceClassName = EngineToos.replaceClassName(engine.getClassName());
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
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
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, replaceClassName, ClassEngine.ObjectDelegateMapName, "Ljava/util/Hashtable;");
        mv.visitLdcInsn(inplType.getName());
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "org/more/core/classcode/Method");
        mv.visitVarInsn(ASTORE, paramCount + 1);//0=this 1=param1
        localVarSize++;
        maxStackSize = (maxStackSize < 2) ? 2 : maxStackSize;
        //Method localMethod=this.$delegateMap.get("xxxxxxx");-------------------------------------------------------------------
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, replaceClassName, "getClass", "()Ljava/lang/Class;");
        //this.getClass();
        mv.visitLdcInsn(name);
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
        //new Class[]{....,....}
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
        mv.visitVarInsn(ASTORE, paramCount + 2);
        localVarSize++;
        // Method localMethod1 = getClass().getMethod("xxx", new Class[]{Integer.Type,Data.class......});------------------------
        mv.visitVarInsn(ALOAD, paramCount + 1);
        mv.visitFieldInsn(GETFIELD, "org/more/core/classcode/Method", "delegate", "Lorg/more/core/classcode/MethodDelegate;");
        mv.visitVarInsn(ALOAD, paramCount + 2);//参数1
        mv.visitVarInsn(ALOAD, 0); //参数3
        //参数4
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
        //localMethod1, localMethod.originalMethod, this, new Object[]{xxx,xxx}
        String desc2 = "Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;";
        mv.visitMethodInsn(INVOKEINTERFACE, "org/more/core/classcode/MethodDelegate", "invoke", "(" + desc2 + ")Ljava/lang/Object;");
        mv.visitVarInsn(ASTORE, paramCount + 3);
        localVarSize++;
        //obj = localMethod.delegate.invoke(localMethod1, this, new Object[] { methodCode });--------
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
        mv.visitVarInsn(ASTORE, 4);
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V");
        mv.visitInsn(ATHROW);
        /* 输出堆栈列表 */
        mv.visitMaxs(maxStackSize, localVarSize + 1);
        mv.visitEnd();
    }
}