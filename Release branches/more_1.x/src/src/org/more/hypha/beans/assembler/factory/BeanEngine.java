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
package org.more.hypha.beans.assembler.factory;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.more.DoesSupportException;
import org.more.InitializationException;
import org.more.InvokeException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.a.AfterCreateExpandPoint;
import org.more.hypha.a.BeforeCreateExpandPoint;
import org.more.hypha.a.ClassByteExpandPoint;
import org.more.hypha.a.ClassTypeExpandPoint;
import org.more.hypha.a.DecoratorExpandPoint;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.AbstractMethodDefine;
import org.more.hypha.beans.AbstractPropertyDefine;
import org.more.hypha.beans.assembler.ClassCache;
/**
 * 该类的职责是负责将{@link AbstractBeanDefine}转换成类型或者Bean实体对象。
 * @version 2011-1-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class BeanEngine {
    private Map<String, BeanBuilder> beanBuilderMap = new HashMap<String, BeanBuilder>();
    //----------------------------------------------------------------------------------------------------------
    /**该方法的返回值决定是否忽略所有扩展点的执行，flase表示不忽略(默认值)。*/
    protected boolean isIgnorePoints() {
        return false;
    };
    /**该方法的返回值决定了是否忽略执行生命周期方法，false表示不忽略(默认值)。*/
    protected boolean isIgnoreLifeMethod() {
        return false;
    };
    protected ClassCache getCatch() {
        return null;//TODO
    };
    /**
     * 
     * @param define
     * @param context
     * @param createParams
     * @return
     */
    public synchronized Class<?> builderType(AbstractBeanDefine define, ApplicationContext applicationContext) throws DoesSupportException, InitializationException {
        String defineType = define.getBeanType();
        String defineID = define.getID();
        ExpandPointManager epm = applicationContext.getExpandPointManager();
        //
        BeanBuilder builder = this.beanBuilderMap.get(defineType);
        if (builder == null)
            throw new DoesSupportException("hypha 不支持的Bean定义类型：" + defineType);
        //1.确定该类型bean是否可以被装载成为java类型。
        if (builder.canbuilder() == false)
            throw new DoesSupportException(defineID + "，的类型定义不能执行装载过程。");
        //--------------------------------------------------------------------------------------------------------------准备阶段
        //2.装载class
        ClassCache cache = this.getCatch();
        byte[] beanBytes = null;
        if (builder.canCache() == true)
            beanBytes = cache.loadClassCode(defineID);//试图从缓存中装载
        if (beanBytes == null) {
            beanBytes = builder.loadBeanBytes(define);
            if (this.isIgnorePoints() == false)
                //如果配置决定不忽略扩展点则执行扩展点代码
                beanBytes = (byte[]) epm.exePoint(ClassByteExpandPoint.class, new Object[] { beanBytes, define, applicationContext });
        }
        if (beanBytes == null)
            throw new NullPointerException("由于无法获取字节码信息，所以无法转换Bean定义成为类型。");
        //3.装载Class类，如果装载不了hypha不会强求
        Class<?> beanType = builder.loadClass(define, beanBytes);
        if (this.isIgnorePoints() == false)
            //如果配置决定不忽略扩展点则执行扩展点代码
            beanType = (Class<?>) epm.exePoint(ClassTypeExpandPoint.class, new Object[] { beanType, define, applicationContext });
        if (beanType == null)
            throw new NullPointerException("丢失Bean类型定义，请检查各扩展点是否正常返回类型。");
        return beanType;
    };
    /**
     * 
     * @param define
     * @param context
     * @param createParams
     * @return
     */
    public Object builderBean(AbstractBeanDefine define, ApplicationContext applicationContext, Object[] params) {
        String defineID = define.getID();
        Class<?> beanType = this.builderType(define, applicationContext);
        ExpandPointManager epm = applicationContext.getExpandPointManager();
        //--------------------------------------------------------------------------------------------------------------创建阶段
        //1.预创建Bean
        Object obj = null;
        if (this.isIgnorePoints() == false)
            obj = epm.exePoint(BeforeCreateExpandPoint.class, new Object[] { beanType, params, define, applicationContext });
        //2.如果没有预创建的对象则执行系统默认的创建过程.
        if (obj == null) {
            AbstractMethodDefine factory = define.factoryMethod();
            Collection<? extends AbstractPropertyDefine> initParam = null;
            if (factory != null) {
                //1.工厂方式
                initParam = factory.getParams();
                //TODO
                //
            } else {
                //2.平常方式
                initParam = define.getInitParams();
                //TODO
                //
            }
        }
        //3.执行创建的后续操作
        if (this.isIgnorePoints() == false)
            //如果配置决定不忽略扩展点则执行扩展点代码
            obj = epm.exePoint(AfterCreateExpandPoint.class, new Object[] { obj, params, define, applicationContext });
        if (obj == null)
            throw new InvokeException("创建[" + defineID + "]，异常不能正常创建或者，装饰的扩展点返回为空。");
        //--------------------------------------------------------------------------------------------------------------初始化阶段
        //4.装饰
        if (this.isIgnorePoints() == false)
            //如果配置决定不忽略扩展点则执行扩展点代码
            obj = epm.exePoint(DecoratorExpandPoint.class, new Object[] { obj, params, define, applicationContext });
        //5.执行初始化
        Class<?> objType = obj.getClass();
        String initMethodName = define.getInitMethod();
        if (initMethodName != null)
            try {
                Method m = objType.getMethod(initMethodName, Object[].class);
                m.invoke(obj, params);//执行初始化
            } catch (Exception e) {
                throw new InitializationException(e);
            }
        //6.代理销毁方法
        //
        //        ProxyFinalizeClassEngine ce = new ProxyFinalizeClassEngine(this);
        //        ce.setBuilderMode(BuilderMode.Propxy);
        //        ce.setSuperClass(objType);
        //        obj = ce.newInstance(obj);
        //7.检测对象类型是否匹配定义类型。
        if (beanType != null)
            return beanType.cast(obj);
        return obj;
    };
};
//class ProxyFinalizeClassEngine extends ClassEngine {
//    private final ProxyFinalizeClassBuilder builder = new ProxyFinalizeClassBuilder();
//    public ProxyFinalizeClassEngine(BeanEngine beanEngine) throws ClassNotFoundException {
//        super(false);
//    };
//    protected ClassBuilder createBuilder(BuilderMode builderMode) {
//        return this.builder;
//    };
//    public Object newInstance(Object propxyBean) throws FormatException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
//        Object obj = super.newInstance(propxyBean);
//        //this.toClass().getMethod("", parameterTypes);
//        return obj;
//    };
//};
//class ProxyFinalizeClassBuilder extends ClassBuilder {
//    protected ClassAdapter acceptClass(ClassWriter classVisitor) {
//        return new ClassAdapter(classVisitor) {
//            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//                if (name.equals("finalize()V") == true)
//                    System.out.println();
//                return super.visitMethod(access, name, desc, signature, exceptions);
//            }
//        };
//    }
//};