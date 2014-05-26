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
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.asm.ClassReader;
import org.more.asm.ClassVisitor;
import org.more.asm.ClassWriter;
import org.more.asm.FieldVisitor;
import org.more.asm.MethodVisitor;
import org.more.asm.Opcodes;
/**
 * 该类负责修改类的字节码附加接口实现方法。
 * 生成类过程
 * visit
 *   1.附加实现接口
 *   2.继承基类
 *   3.修改新类类名
 * visitMethod
 *   1.修改方法名为
 *   2.输出代理方法
 *   3.增加本地方法集合
 * visitEnd
 *   1.输出Propxy的构造方法
 *   2.输出简单属性
 *   3.输出委托属性
 *   4.输出委托方法
 * @version 2010-8-12
 * @author 赵永春 (zyc@hasor.net)
 */
class BuilderClassAdapter extends ClassVisitor implements Opcodes {
    //1.ClassAdapter使用的类对象。
    private ClassBuilder        classBuilder              = null;
    private ClassEngine         classEngine               = null;
    private String              asmClassName              = null;
    private ArrayList<String>   localMethodList           = null;
    //2.用于保存渲染字节码的结果
    private ArrayList<String>   renderMethodList          = null;
    private ArrayList<String>   renderDelegateList        = null;
    private ArrayList<String>   renderDelegatePropxyList  = null;
    //3.当工作在代理模式下代理字段的名称。
    public final static String  SuperPropxyName           = "$propxyObject";
    //4.定义了代理属性，代理方法的字段名称和类型。
    public final static String  DelegateArrayName         = "$delegateArray";
    private final static String DelegateArrayType         = EngineToos.toAsmType(MethodDelegate[].class);
    public final static String  DelegateMethodArrayName   = "$delegateMethodArray";
    private final static String DelegateMethodArrayType   = EngineToos.toAsmType(Method[].class);
    //5.定义了代理属性的字段名称。
    public final static String  PropertyArrayName         = "$propertyArray";
    private final static String PropertyDelegateArrayType = EngineToos.toAsmType(PropertyDelegate[].class);
    //6.是否配置的标记字段名
    public final static String  ConfigMarkName            = "$configMark";
    //
    public BuilderClassAdapter(ClassVisitor cv, ClassBuilder classBuilder) {
        super(ASM4, cv);
        this.classBuilder = classBuilder;
        this.classEngine = classBuilder.getClassEngine();
        this.localMethodList = new ArrayList<String>();
        this.renderMethodList = new ArrayList<String>();
        this.renderDelegateList = new ArrayList<String>();
        this.renderDelegatePropxyList = new ArrayList<String>();
        this.asmClassName = this.classBuilder.getClassEngine().getAsmClassName();
    }
    /***/
    public ArrayList<String> getRenderMethodList() {
        return this.renderMethodList;
    }
    /***/
    public ArrayList<String> getRenderDelegateList() {
        return this.renderDelegateList;
    }
    /***/
    public ArrayList<String> getRenderDelegatePropxyList() {
        return this.renderDelegatePropxyList;
    }
    //
    //1.附加实现接口
    //2.继承基类
    //3.修改新类类名
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        //1.附加接口实现
        if (this.classBuilder.isAddDelegate() == true) {
            this.putSetMethod(DelegateArrayName, DelegateArrayType);
            this.putSetMethod(DelegateMethodArrayName, DelegateMethodArrayType);
            ArrayList<String> al = new ArrayList<String>(interfaces.length + 10);
            Collections.addAll(al, interfaces);//已经实现的接口
            Collections.addAll(al, this.classBuilder.getDelegateString());//附加接口实现
            Collections.addAll(renderDelegateList, this.classBuilder.getDelegateString());//附加接口实现
            //转换List为Array
            interfaces = new String[al.size()];
            al.toArray(interfaces);
        }
        //2.继承基类
        superName = name;
        //3.修改新类类名
        name = this.asmClassName;
        super.visit(version, ACC_PUBLIC, name, signature, superName, interfaces);
    }
    //
    //当是Propxy模式下时候就忽略字段的输出。
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (this.classEngine.getBuilderMode() == BuilderMode.Propxy)
            return null;
        return super.visitField(access, name, desc, signature, value);
    }
    //
    //1.方法忽略策略
    //2.输出已有方法，Super和Propxy。
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        BuilderMode builderMode = this.classEngine.getBuilderMode();
        String asmSuperClassName = this.classBuilder.getClassEngine().getAsmSuperClassName();
        String fullDesc = name + desc;
        MethodStrategy methodStrategy = this.classEngine.getMethodStrategy();
        //
        //1.忽略特定描述方法
        if ((access | ACC_PRIVATE) == access) //忽略private方法。
            return null;
        if ((access | ACC_STATIC) == access)//忽略static描述方法。
            return null;
        if ((access | ACC_FINAL) == access)//忽略final描述方法。
            return null;
        if ((access | ACC_NATIVE) == access)//忽略native描述方法。
            if (fullDesc.equals("hashCode()I") == false && fullDesc.equals("clone()Ljava/lang/Object;") == false)
                return null;
            else
                access = access - ACC_NATIVE;
        if (builderMode == BuilderMode.Propxy) {//当在Propxy。
            if (name.equals("<init>") == true)//忽略Propxy下的所有构造方法。
                return null;
            if ((access | ACC_PROTECTED) == access)//忽略Propxy下的保护方法。
                return null;
        }
        //2.准备输出方法数据
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = m.group(2);
        //
        //3.执行方法忽略策略
        Class<?> superClass = this.classEngine.getSuperClass();
        boolean isConstructor = false;
        if (name.equals("<init>") == true)
            isConstructor = true;
        Class<?>[] paramTypes = EngineToos.toJavaType(asmParams, this.classEngine.getRootClassLoader());
        Object method = null;
        if (isConstructor == true)
            try {
                method = superClass.getConstructor(paramTypes);
            } catch (Exception e) {/*忽略*/}
        else
            method = EngineToos.findMethod(superClass, name, paramTypes);
        if (method != null)
            if (methodStrategy.isIgnore(superClass, method, isConstructor) == true)//其他策略
                return null;
        //
        //4.输出方法
        int maxLocals = 1;
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        mv.visitCode();
        if (builderMode == BuilderMode.Super) {
            //Super 如果是继承方式调用则使用super.invoke调用。
            mv.visitVarInsn(ALOAD, 0);
            for (int i = 0; i < asmParams.length; i++)
                mv.visitVarInsn(EngineToos.getLoad(asmParams[i]), i + 1);
            mv.visitMethodInsn(INVOKESPECIAL, asmSuperClassName, name, desc);
            maxLocals += asmParams.length;
        } else {
            //Propxy 如果是代理方式则使用this.$propxyObject.invoke。
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, asmClassName, SuperPropxyName, "L" + asmSuperClassName + ";");
            for (int i = 0; i < asmParams.length; i++)
                mv.visitVarInsn(EngineToos.getLoad(asmParams[i]), i + 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, asmSuperClassName, name, desc);
            maxLocals += (asmParams.length + 1);
        }
        //5.处理方法调用的返回值return。
        if (asmReturns.equals("V") == true)
            mv.visitInsn(RETURN);
        else
            mv.visitInsn(EngineToos.getReturn(asmReturns));
        //6.结束方法输出，确定方法堆栈等信息。
        mv.visitMaxs(maxLocals + 1, maxLocals + 1);
        mv.visitEnd();
        //7.将已经处理的方法添加到本地方法表中并返回，在visitInterfaceMethod方法中会需要这个信息。
        localMethodList.add(name + desc);
        return null;
    }
    //
    //1.输出Propxy的构造方法
    //2.输出简单属性
    //3.输出委托属性
    //4.输出委托方法
    public void visitEnd() {
        //
        //1.输出Propxy的构造方法。
        if (this.classEngine.getBuilderMode() == BuilderMode.Propxy) {
            String asmSuperName = EngineToos.toAsmType(this.classEngine.getSuperClass());
            String asmSuperName2 = EngineToos.asmTypeToType(asmSuperName);
            //
            FieldVisitor fv = super.visitField(ACC_PRIVATE, SuperPropxyName, asmSuperName, null, null);
            fv.visitEnd();
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "(" + asmSuperName + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitMethodInsn(INVOKESPECIAL, asmSuperName2, "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitVarInsn(ALOAD, 1);//装载参数
            mv.visitFieldInsn(PUTFIELD, EngineToos.asmTypeToType(asmClassName), SuperPropxyName, asmSuperName);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        //
        //2.输出属性
        if (this.classBuilder.isAddFields() == true) {
            PropertyStrategy propertyStrategy = this.classEngine.getPropertyStrategy();
            //简单属性。
            String[] simpleFields = this.classBuilder.getSimpleFields();
            if (simpleFields != null)
                for (String field : simpleFields) {
                    Class<?> fieldType = this.classEngine.getSimplePropertyType(field);
                    if (propertyStrategy.isIgnore(field, fieldType, false) == true)
                        continue;
                    boolean readOnly = propertyStrategy.isReadOnly(field, fieldType, false);
                    boolean writeOnly = propertyStrategy.isWriteOnly(field, fieldType, false);
                    this.putSimpleProperty(field, fieldType, writeOnly, readOnly);
                }
            //委托属性。
            String[] delegateFields = this.classBuilder.getDelegateFields();
            if (delegateFields != null) {
                super.visitField(ACC_PRIVATE, PropertyArrayName, PropertyDelegateArrayType, null, null);
                this.putSetMethod(PropertyArrayName, PropertyDelegateArrayType);
                for (int i = 0; i < delegateFields.length; i++) {
                    String field = delegateFields[i];
                    PropertyDelegate<?> fieldDelegate = this.classEngine.getDelegateProperty(field);
                    Class<?> delegateType = fieldDelegate.getType();
                    if (propertyStrategy.isIgnore(field, delegateType, true) == true)
                        continue;
                    this.renderDelegatePropxyList.add(field);
                    boolean readOnly = propertyStrategy.isReadOnly(field, delegateType, false);
                    boolean writeOnly = propertyStrategy.isWriteOnly(field, delegateType, false);
                    this.putDelegateProperty(i, field, fieldDelegate, writeOnly, readOnly);
                }
            }
        }
        //PropertyArrayName
        //3.输出委托方法。
        if (this.classBuilder.isAddDelegate() == true) {
            //
            super.visitField(ACC_PRIVATE, DelegateArrayName, DelegateArrayType, null, null);
            super.visitField(ACC_PRIVATE, DelegateMethodArrayName, DelegateMethodArrayType, null, null);
            //
            Class<?>[] delegateType = this.classBuilder.getDelegateType();
            for (int i = 0; i < delegateType.length; i++) {
                final Class<?> type = delegateType[i];
                final int classIndex = i;
                try {
                    ClassReader reader = new ClassReader(EngineToos.getClassInputStream(type));//创建ClassReader
                    final BuilderClassAdapter adapter = this;
                    //扫描附加接口方法
                    reader.accept(new ClassVisitor(Opcodes.ASM4, new ClassWriter(ClassWriter.COMPUTE_MAXS)) {
                        private int methodIndex = 0;
                        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                            if (adapter.localMethodList.contains(name + desc) == true)
                                return null;//如果本地方法集合中存在该方法则忽略输出。
                            String fullDesc = name + desc;
                            MethodVisitor mv = adapter.cv.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
                            adapter.visitInterfaceMethod(classIndex, methodIndex, adapter, mv, type, name, desc);//输出代理方法调用
                            adapter.localMethodList.add(fullDesc);//加入以处理方法表
                            adapter.renderMethodList.add(fullDesc);//加入需要ioc其Method类型的方法表
                            methodIndex++;
                            return mv;
                        }
                    }, ClassReader.SKIP_DEBUG);
                    //try end
                } catch (Exception e) {
                    throw new InvokeException("在扫描输出委托[" + type.getName() + "]时候发生异常。");
                }
            }
            //
        }
        //4.输出配置标记
        //输出是否经过配置的标记
        this.putSimpleProperty(ConfigMarkName, Boolean.class, false, false);
        super.visitEnd();
    }
    //
    //输出简单属性
    private void putSimpleProperty(String propertyName, Class<?> propertyType, boolean isWriteOnly, boolean isReadOnly) {
        String asmFieldType = EngineToos.toAsmType(propertyType);
        FieldVisitor fv = super.visitField(ACC_PRIVATE, propertyName, asmFieldType, null, null);
        fv.visitEnd();
        if (isWriteOnly == false)
            this.putGetMethod(propertyName, asmFieldType);//get
        if (isReadOnly == false)
            this.putSetMethod(propertyName, asmFieldType);//set
    }
    //
    //输出委托属性
    private void putDelegateProperty(int index, String propertyName, PropertyDelegate<?> fieldDelegate, boolean isWriteOnly, boolean isReadOnly) {
        String asmDelegateType2 = EngineToos.replaceClassName(PropertyDelegate.class.getName());
        //
        Class<?> javaFieldType = fieldDelegate.getType();
        String asmFieldType = EngineToos.toAsmType(javaFieldType);
        String asmFieldType2 = EngineToos.replaceClassName(javaFieldType.getName());
        if (isWriteOnly == false) {
            //get
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "get" + EngineToos.toUpperCase(propertyName), "()" + asmFieldType, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitFieldInsn(GETFIELD, this.asmClassName, PropertyArrayName, PropertyDelegateArrayType);
            mv.visitIntInsn(BIPUSH, index);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitMethodInsn(INVOKEINTERFACE, asmDelegateType2, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, asmFieldType2);
            mv.visitInsn(EngineToos.getReturn(asmFieldType));
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        if (isReadOnly == false) {
            //set
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "set" + EngineToos.toUpperCase(propertyName), "(" + asmFieldType + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitFieldInsn(GETFIELD, this.asmClassName, PropertyArrayName, PropertyDelegateArrayType);
            mv.visitIntInsn(BIPUSH, index);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 0);//装载this
            mv.visitVarInsn(ALOAD, 1);//装载param1
            mv.visitTypeInsn(CHECKCAST, asmFieldType2);
            mv.visitMethodInsn(INVOKEINTERFACE, asmDelegateType2, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }
    //
    //实现接口附加
    private void visitInterfaceMethod(int classIndex, int methodIndex, BuilderClassAdapter adapter, MethodVisitor mv, Class<?> type, String name, String desc) {
        Pattern p = Pattern.compile("\\((.*)\\)(.*)");
        Matcher m = p.matcher(desc);
        m.find();
        String[] asmParams = EngineToos.splitAsmType(m.group(1));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String asmReturns = EngineToos.asmTypeToType(m.group(2));
        int paramCount = asmParams.length;
        int localVarSize = paramCount;//方法变量表大小
        int maxStackSize = 0;//方法最大堆栈大小
        //-----------------------------------------------------------------------------------------------------------------------
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, this.asmClassName, DelegateArrayName, DelegateArrayType);
        mv.visitIntInsn(BIPUSH, classIndex);
        mv.visitInsn(AALOAD);
        //参数1
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, this.asmClassName, DelegateMethodArrayName, DelegateMethodArrayType);
        mv.visitIntInsn(BIPUSH, methodIndex);
        mv.visitInsn(AALOAD);
        //参数2
        mv.visitVarInsn(ALOAD, 0);
        //参数3
        mv.visitIntInsn(BIPUSH, paramCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < paramCount; i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            String asmType = asmParams[i];
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
            maxStackSize = (maxStackSize < 5 + i) ? 5 + i : maxStackSize;
        }
        //调用
        String delegateType2 = EngineToos.replaceClassName(MethodDelegate.class.getName());
        mv.visitMethodInsn(INVOKEINTERFACE, delegateType2, "invoke", "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        //return
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
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
        } else {
            mv.visitTypeInsn(CHECKCAST, asmReturns);
            mv.visitInsn(ARETURN);
        }
        /* 输出堆栈列表 */
        mv.visitMaxs(maxStackSize, localVarSize + 1);
        mv.visitEnd();
    }
    //
    //公开某个字段的set方法
    private void putSetMethod(String propertyName, String asmFieldType) {
        MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "set" + EngineToos.toUpperCase(propertyName), "(" + asmFieldType + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);//装载this
        mv.visitVarInsn(ALOAD, 1);//装载参数
        mv.visitFieldInsn(PUTFIELD, this.asmClassName, propertyName, asmFieldType);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
    //
    //公开某个字段的get方法
    private void putGetMethod(String propertyName, String asmFieldType) {
        //get
        MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "get" + EngineToos.toUpperCase(propertyName), "()" + asmFieldType, null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);//装载this
        mv.visitFieldInsn(GETFIELD, this.asmClassName, propertyName, asmFieldType);
        mv.visitInsn(EngineToos.getReturn(asmFieldType));
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}