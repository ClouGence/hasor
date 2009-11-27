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
package org.more.beans.core.injection;
import java.util.ArrayList;
import java.util.Collections;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.PropVarValue;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.ClassWriter;
import org.more.core.asm.MethodVisitor;
import org.more.core.asm.Opcodes;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.EngineToos;
import org.more.core.classcode.ClassEngine.BuilderMode;
import org.more.util.StringConvert;
/**
 * 在Fact方式下首次请求装载类时more.beans会生成一个属性注入类，并且使用这个属性注入类进行注入。
 * 这个属性注入类的代码是完全由more.classcode工具生成，生成的类代码使用最原始的方式对bean进行get/set。
 * Fact方式比较Ioc方式省略了反射注入的过程，Fact采用直接调用方法进行属性注入，从而增加运行速度。经过测试
 * fact方式的运行速度与原始get/set运行速度相当接近，100万次进行基本类型属性注入速度只相差15毫秒落后。
 * 在1000万次注入测试下get/set消耗了312毫秒而fact消耗了843毫秒，ioc方式则需要消耗18.3秒。
 * 这可以证明在Fact方式下会有很好的属性注入运行效率，但是Fact也会对每个要求Fact的bean生成一个注入器。
 * 这也就是说在fact方式下会比ioc方式增加少量内存消耗。生成的注入器被保存在{@link BeanDefinition}的属性中。
 * 只有{@link BeanDefinition}对象被缓存才有上述运行效率，否则fact的效率可能远远不足ioc。
 * <br/>Date : 2009-11-7
 * @author 赵永春
 */
public class FactInjection implements Injection {
    //========================================================================================Field
    /** 属性缓存对象，缓存属性名。 */
    private String factCatchName = "$more_Injection_fact";
    //==========================================================================================Job
    /**
     * 取得缓存的fact注入代理类生成引擎，如果不存在缓存则创建这个引擎并且生成代理类。
     * 根据代理类引擎创建一个代理类对象并且执行代理类对象的代理注入方法。注入方法会使用最原始的get/set方式进行注入。
     * 如果注入的属性请求引用对象则会引起对context的getBean调用。在fact注入模式下代理类只会生成。
     */
    @Override
    public Object ioc(final Object object, final Object[] params, final BeanDefinition definition, final ResourceBeanFactory context) throws Exception {
        FactIoc fact = null;
        if (definition.containsKey(this.factCatchName) == true)
            //获取代理注入类生成引擎
            fact = (FactIoc) definition.get(this.factCatchName);
        else {
            //创建ClassEngine引擎
            /*
             * 下面三行代码用于解决配置了AOP情况下需要Fact方式执行Ioc，无法获取iocbean的Class对象问题。
             * 下面由于ClassEngine使用的父类装载器是context.getBeanClassLoader()因此直接获取，需要ioc
             * 的bean的ClassLoader就相当于获取到了context.getBeanClassLoader()。
             * 他们的关系是如下的:
             * systemClassLoader
             *   context.getBeanClassLoader()
             *     ClassEngine
             * 有了这三行代码more.beans 提供了Fact方式注入下的AOP、impl方面支持。
             */
            ClassLoader beanLoader = object.getClass().getClassLoader();
            if (beanLoader instanceof ClassEngine == false)
                beanLoader = context.getBeanClassLoader();
            //
            ClassEngine engine = new ClassEngine(beanLoader) {
                protected ClassAdapter acceptClass(ClassWriter classWriter) {
                    return new FactClassAdapter(classWriter, object.getClass().getName(), definition);
                }
            };
            //设置引擎基本信息
            engine.setSuperClass(FactIocObject.class);//由于不支持rt.jar中的类因此需要一个假Object对象。
            engine.setMode(BuilderMode.Super);
            engine.forceBuilderClass();
            fact = (FactIoc) engine.newInstance(null);
            //缓存引擎生成对象
            definition.setAttribute(this.factCatchName, fact);
        }
        /*执行fact注入*/
        fact.ioc(object, params, definition, context);
        return object;
    }
}
/** 负责改写ioc方法以实现fact方式注入。*/
class FactClassAdapter extends ClassAdapter implements Opcodes {
    //========================================================================================Field
    private BeanDefinition definition = null; //Bean
    private String         className  = null; //注入类类型
    //==================================================================================Constructor
    public FactClassAdapter(ClassVisitor cv, String objectClassName, BeanDefinition definition) {
        super(cv);
        this.className = EngineToos.replaceClassName(objectClassName);
        this.definition = definition;
    }
    //==========================================================================================Job
    /** 附加接口实现 */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        ArrayList<String> al = new ArrayList<String>(0);
        Collections.addAll(al, interfaces);
        al.add(EngineToos.replaceClassName(FactIoc.class.getName()));
        String[] ins = new String[al.size()];
        al.toArray(ins);
        super.visit(version, access, name, signature, superName, ins);
    }
    @Override
    public void visitEnd() {
        String desc = EngineToos.toAsmType(new Class<?>[] { Object.class, Object[].class, BeanDefinition.class, ResourceBeanFactory.class });
        MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "ioc", "(" + desc + ")V", null, new String[] { "java/lang/Throwable" });
        //生成注入方法
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, this.className);
        mv.visitVarInsn(ASTORE, 5);
        BeanProperty[] bps = this.definition.getPropertys();
        for (int i = 0; i < bps.length; i++) {
            mv.visitVarInsn(ALOAD, 5);
            //String propTypeByASM = null;
            BeanProperty prop = bps[i];
            //
            String propType = prop.getPropType();
            if (propType == BeanProperty.TS_Integer) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitIntInsn(BIPUSH, StringConvert.parseInt(var.getValue()));
                this.invokeMethod(mv, prop, "I");
            } else if (propType == BeanProperty.TS_Byte) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitIntInsn(BIPUSH, StringConvert.parseByte(var.getValue()));
                this.invokeMethod(mv, prop, "B");
            } else if (propType == BeanProperty.TS_Char) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                if (var.getValue() == null)
                    mv.visitIntInsn(BIPUSH, 0);
                else
                    mv.visitIntInsn(BIPUSH, var.getValue().charAt(0));
                this.invokeMethod(mv, prop, "C");
            } else if (propType == BeanProperty.TS_Double) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitLdcInsn(StringConvert.parseDouble(var.getValue()));
                this.invokeMethod(mv, prop, "D");
            } else if (propType == BeanProperty.TS_Float) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitLdcInsn(StringConvert.parseFloat(var.getValue()));
                this.invokeMethod(mv, prop, "F");
            } else if (propType == BeanProperty.TS_Long) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitLdcInsn(StringConvert.parseLong(var.getValue()));
                this.invokeMethod(mv, prop, "J");
            } else if (propType == BeanProperty.TS_Short) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitIntInsn(BIPUSH, StringConvert.parseShort(var.getValue()));
                this.invokeMethod(mv, prop, "S");
            } else if (propType == BeanProperty.TS_Boolean) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                boolean bool = StringConvert.parseBoolean(var.getValue());
                mv.visitInsn((bool == true) ? ICONST_1 : ICONST_0);
                this.invokeMethod(mv, prop, "Z");
            } else if (propType == BeanProperty.TS_String) {
                PropVarValue var = (PropVarValue) prop.getRefValue();
                mv.visitLdcInsn(var.getValue());
                this.invokeMethod(mv, prop, "Ljava/lang/String;");
            } else {
                /*in          Object[]、BeanDefinition、ResourceBeanFactory */
                /*out Object、Object[]、BeanDefinition、ResourceBeanFactory、BeanProperty*/
                StringBuffer sb = new StringBuffer(prop.getName());
                char firstChar = sb.charAt(0);
                sb.delete(0, 1);
                firstChar = (char) ((firstChar >= 97) ? firstChar - 32 : firstChar);
                sb.insert(0, firstChar);
                sb.insert(0, "set");
                mv.visitLdcInsn(sb.toString());
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/more/beans/info/BeanDefinition", "getPropertys", "()[Lorg/more/beans/info/BeanProperty;");
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(AALOAD);
                String descStr = EngineToos.toAsmType(new Class<?>[] { String.class, Object.class, Object[].class, BeanDefinition.class, ResourceBeanFactory.class, BeanProperty.class });
                mv.visitMethodInsn(INVOKESTATIC, "org/more/beans/core/TypeParser", "passerType", "(" + descStr + ")Ljava/lang/Object;");
                mv.visitInsn(POP);
            }
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 4);
        mv.visitEnd();
        super.visitEnd();
    }
    private void invokeMethod(MethodVisitor mv, BeanProperty prop, String propTypeByASM) {
        //转换首字母大写
        StringBuffer sb = new StringBuffer(prop.getName());
        char firstChar = sb.charAt(0);
        sb.delete(0, 1);
        firstChar = (char) ((firstChar >= 97) ? firstChar - 32 : firstChar);
        sb.insert(0, firstChar);
        sb.insert(0, "set");
        mv.visitMethodInsn(INVOKEVIRTUAL, this.className, sb.toString(), "(" + propTypeByASM + ")V");
        //this.setMeyhodName(...);
    }
}