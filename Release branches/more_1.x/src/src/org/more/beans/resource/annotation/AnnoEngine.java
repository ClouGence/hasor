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
package org.more.beans.resource.annotation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.more.InvokeException;
/**
 * 注解扫描器，该类负责扫描流程，扫描结果通过AnnoContextStack根堆栈的context字段所表示。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoEngine {
    /**  */
    private static final long serialVersionUID = -780028127861834511L;
    //==========================================================================================Job
    /**执行扫描任务。*/
    public AnnoContextStack runTask(Class<?> type, AnnoProcess process) throws InvokeException {
        /*扫描过程Class->Field->Constructor->Param->Method->Param*/
        Class<?> atType = type;
        Annotation[] annos = null;
        AnnoContextStack stack = new AnnoContextStack(null, atType);
        //一、解析Class
        annos = atType.getAnnotations();
        if (annos != null)
            for (Annotation anno : annos)
                process.doAnnotation(anno, atType, AnnoScopeEnum.Anno_Type, stack);
        //二、解析Field
        List<Field> fields_1 = Arrays.asList(atType.getFields());
        List<Field> fields_2 = Arrays.asList(atType.getDeclaredFields());
        ArrayList<Field> fields = new ArrayList<Field>(fields_1.size() + fields_2.size());
        for (Field f : fields_1)
            if (fields.contains(f) == false)
                fields.add(f);
        for (Field f : fields_2)
            if (fields.contains(f) == false)
                fields.add(f);
        for (Field f : fields) {
            annos = f.getAnnotations();
            if (annos != null)
                for (Annotation anno : annos)
                    process.doAnnotation(anno, f, AnnoScopeEnum.Anno_Field, new AnnoContextStack(stack, atType));
        }
        //三、解析Constructor
        Constructor<?>[] constructors = atType.getConstructors();
        for (Constructor<?> c : constructors) {
            annos = c.getAnnotations();
            AnnoContextStack c_stack = new AnnoContextStack(stack, atType);
            if (annos != null)
                for (Annotation anno : annos)
                    process.doAnnotation(anno, c, AnnoScopeEnum.Anno_Constructor, c_stack);
            //四、解析Constructor-Param
            Annotation[][] annoss = c.getParameterAnnotations();
            Type[] params = c.getGenericParameterTypes();
            for (int i = 0; i < params.length; i++) {
                Type p = params[i];
                Annotation[] pAnnos = annoss[i];
                for (int j = 0; j < pAnnos.length; j++)
                    process.doAnnotation(pAnnos[i], p, AnnoScopeEnum.Anno_Param, new AnnoContextStack(c_stack, atType));
            }
        }
        //三、解析Method
        List<Method> methods_1 = Arrays.asList(atType.getMethods());
        List<Method> methods_2 = Arrays.asList(atType.getDeclaredMethods());
        ArrayList<Method> methods = new ArrayList<Method>(methods_1.size() + methods_2.size());
        for (Method m : methods_1)
            if (methods.contains(m) == false)
                methods.add(m);
        for (Method m : methods_2)
            if (methods.contains(m) == false)
                methods.add(m);
        for (Method m : methods) {
            annos = m.getAnnotations();
            AnnoContextStack m_stack = new AnnoContextStack(stack, atType);
            if (annos != null)
                for (Annotation anno : annos)
                    process.doAnnotation(anno, m, AnnoScopeEnum.Anno_Method, m_stack);
            //四、解析Constructor-Param
            Annotation[][] annoss = m.getParameterAnnotations();
            Type[] params = m.getGenericParameterTypes();
            for (int i = 0; i < params.length; i++) {
                Type p = params[i];
                Annotation[] pAnnos = annoss[i];
                for (int j = 0; j < pAnnos.length; j++)
                    process.doAnnotation(pAnnos[i], p, AnnoScopeEnum.Anno_Param, new AnnoContextStack(m_stack, atType));
            }
        }
        return stack;
    }
}