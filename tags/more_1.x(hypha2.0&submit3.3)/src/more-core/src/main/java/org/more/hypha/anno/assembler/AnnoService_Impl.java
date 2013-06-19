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
package org.more.hypha.anno.assembler;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.more.core.asm.ClassReader;
import org.more.core.asm.ClassWriter;
import org.more.hypha.DefineResource;
import org.more.hypha.anno.AnnoService;
import org.more.hypha.anno.KeepWatchParser;
import org.more.hypha.anno.xml.EV_Class;
import org.more.hypha.commons.AbstractService;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 注解插件接口{@link AnnoService}的实现类。
 * @version 2010-10-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoService_Impl extends AbstractService implements AnnoService {
    /**要注册的插件名*/
    private XmlDefineResource                        config      = null;
    private ArrayList<String>                        parserTypes = new ArrayList<String>();
    private HashMap<Class<?>, List<KeepWatchParser>> parserMap   = new HashMap<Class<?>, List<KeepWatchParser>>();
    //
    public AnnoService_Impl(XmlDefineResource config) {
        this.config = config;
    }
    //
    public void start() {};
    public void stop() {};
    //
    public XmlDefineResource getTarget() {
        return this.config;
    }
    /**
     * 注册一个注解解析器，该方法会通知注解解析系统，当遇到某个特定的注解时这个类交付这个注解处理器进行处理。
     * @param anno 注册的注解类型。
     * @param watch 解析器。
     */
    public void registerAnnoKeepWatch(Class<? extends Annotation> anno, KeepWatchParser watch) {
        //String annoType = anno.getName();
        List<KeepWatchParser> parserList = null;
        if (this.parserTypes.contains(anno) == false) {
            parserList = new ArrayList<KeepWatchParser>();
            this.parserMap.put(anno, parserList);
            this.parserTypes.add(anno.getName());
        } else
            parserList = this.parserMap.get(anno);
        parserList.add(watch);
    };
    public boolean containsKeepWatchParser(String annoType) {
        return this.parserTypes.contains(annoType);
    };
    /**根据Anno类型定义确定获取注册在其类型上的解析器。*/
    public Collection<KeepWatchParser> getAnnoKeepWatch(String annoType) {
        return this.parserMap.get(annoType);
    };
    /**通知aop解析器解析这个类，className参数表示的是预解析的类名。该类是通过{@link DefineResource}中的ClassLoader装载的。*/
    public synchronized void parserClass(String className) throws ClassNotFoundException, IOException {
        String classPath = className.replace(".", "/") + ".class";
        InputStream is = ClassLoader.getSystemResourceAsStream(classPath);
        this.parserClass(is);
    };
    /**通知aop解析器解析这个类，classInputStream参数表示的是预解析的类输入流。*/
    public synchronized void parserClass(InputStream classInputStream) throws ClassNotFoundException, IOException {
        //2.通知引擎扫描这个类，确定是否有必要解析。使用ASM进行扫描增加速度。
        ClassReader reader = new ClassReader(classInputStream);
        EV_Class ev = new EV_Class(this, new ClassWriter(ClassWriter.COMPUTE_MAXS));
        reader.accept(ev, ClassReader.SKIP_DEBUG);
        //3.通知引擎执行解析，这个类中包含具备解析条件的注解。
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (ev.isMark() == true)
            this.parser_Class(loader.loadClass(ev.getClassName()));
    };
    /*--------------------------------------------------*/
    private void pushEvent(List<KeepWatchParser> kwpList, Object target, Annotation annoData) {
        if (kwpList != null)
            for (KeepWatchParser kwp : kwpList)
                kwp.process(target, annoData, this.config);
    }
    /**正式解析类。*/
    private void parser_Class(Class<?> classType) throws ClassNotFoundException {
        Annotation[] annos = classType.getAnnotations();
        if (annos != null)
            for (Annotation a : annos)
                this.parser_Annotation(classType, a);
        Constructor<?>[] cs = classType.getDeclaredConstructors();
        if (cs != null)
            for (Constructor<?> c : cs)
                this.parser_Constructor(c);
        Field[] fs = classType.getDeclaredFields();
        if (fs != null)
            for (Field f : fs)
                this.parser_Field(f);
        Method[] ms = classType.getDeclaredMethods();
        if (ms != null)
            for (Method m : ms)
                this.parser_Method(m);
    }
    //该方法会递归解析注解，并且解析注解中的注解。
    private void parser_Annotation(Object target, Annotation annoData) {
        Class<? extends Annotation> annoType = annoData.annotationType();
        if (annoType == target)
            return;//该处return是为了避免注解递归。
        List<KeepWatchParser> kwpList = this.parserMap.get(annoType);
        this.pushEvent(kwpList, target, annoData);
        //
        Annotation[] annos = annoType.getAnnotations();
        if (annos != null)
            for (Annotation a : annos)
                this.parser_Annotation(a.annotationType(), a);
    }
    //该方法会解析字段的注解。
    private void parser_Field(Field field) {
        Annotation[] annos = field.getAnnotations();
        if (annos != null)
            for (Annotation a : annos)
                this.parser_Annotation(field, a);
    }
    private void parser_Method(Method method) {
        Annotation[] annos = method.getAnnotations();
        if (annos != null)
            for (Annotation a : annos)
                this.parser_Annotation(method, a);
        Type[] types = method.getGenericParameterTypes();
        Annotation[][] typesannos = method.getParameterAnnotations();
        if (types != null)
            for (int i = 0; i < types.length; i++) {
                //一个参数的
                Type type = types[i];
                Annotation[] typesanno = typesannos[i];
                for (int j = 0; j < typesanno.length; j++)
                    this.parser_Annotation(new Object[] { i, type }, typesanno[i]);
            }
    }
    private void parser_Constructor(Constructor<?> anno) {
        Annotation[] annos = anno.getAnnotations();
        if (annos != null)
            for (Annotation a : annos)
                this.parser_Annotation(anno, a);
        Type[] types = anno.getGenericParameterTypes();
        Annotation[][] typesannos = anno.getParameterAnnotations();
        if (types != null)
            for (int i = 0; i < types.length; i++) {
                //一个参数的
                Type type = types[i];
                Annotation[] typesanno = typesannos[i];
                for (int j = 0; j < typesanno.length; j++)
                    this.parser_Annotation(new Object[] { i, type }, typesanno[i]);
            }
    }
}