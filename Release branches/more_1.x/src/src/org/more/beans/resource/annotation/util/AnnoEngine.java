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
package org.more.beans.resource.annotation.util;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    public AnnoContextStack runTask(Class<?> type, AnnoProcess process, Object stackRootContext) throws InvokeException {
        /*
         * 扫描过程：
         * 1.begin Class->
         * 2.     begin Field->end Field
         * 3.     begin Constructor->
         * 4.         begin Param->end Param
         * 5.     end Constructor
         * 6.     begin Method->
         * 7.         begin Param->end Param
         * 8.     end Method->
         * 9.end Class->
         */
        //一、解析Class
        Class<?> atType = type;
        AnnoContextStack stack = new AnnoContextStack(null, atType, AnnoScopeEnum.Anno_Type);
        stack.context = stackRootContext;
        Annotation[] class_anno = atType.getAnnotations();
        for (Annotation a : class_anno)
            process.beginAnnotation(a, atType, stack);
        {
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
                Annotation[] field_anno = f.getAnnotations();
                if (field_anno != null)
                    for (Annotation a : field_anno) {
                        stack = new AnnoContextStack(stack, atType, AnnoScopeEnum.Anno_Field);
                        process.beginAnnotation(a, new Object[] { atType, f }, stack);
                        process.endAnnotation(a, new Object[] { atType, f }, stack);
                        stack = stack.getParent();
                    }
            }
            //三、解析Constructor
            Constructor<?>[] constructors = atType.getConstructors();
            for (Constructor<?> c : constructors) {
                //
                Annotation[] constructor_anno = c.getAnnotations();
                stack = new AnnoContextStack(stack, atType, AnnoScopeEnum.Anno_Constructor);
                for (Annotation a : constructor_anno)
                    process.beginAnnotation(a, c, stack);
                {
                    //四、解析Constructor-Param
                    Annotation[][] param_anno = c.getParameterAnnotations();
                    Class<?>[] paramTypes = c.getParameterTypes();
                    for (int i = 0; i < paramTypes.length; i++) {
                        Class<?> paramType = paramTypes[i];
                        Annotation[] paramType_Anno = param_anno[i];
                        for (Annotation a : paramType_Anno) {
                            stack = new AnnoContextStack(stack, atType, AnnoScopeEnum.Anno_Param);
                            process.beginAnnotation(a, new Object[] { c, paramType }, stack);
                            process.beginAnnotation(a, new Object[] { c, paramType }, stack);
                            stack = stack.getParent();
                        }
                    }
                }
                for (Annotation a : constructor_anno)
                    process.endAnnotation(a, c, stack);
                stack = stack.getParent();
            }
        }
        //五、解析Method
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
            Annotation[] method_anno = m.getAnnotations();
            stack = new AnnoContextStack(stack, atType, AnnoScopeEnum.Anno_Method);
            for (Annotation a : method_anno)
                process.beginAnnotation(a, m, stack);
            {
                //六、解析Method-Param
                Annotation[][] param_anno = m.getParameterAnnotations();
                Class<?>[] paramTypes = m.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> paramType = paramTypes[i];
                    Annotation[] paramType_Anno = param_anno[i];
                    for (Annotation a : paramType_Anno) {
                        stack = new AnnoContextStack(stack, atType, AnnoScopeEnum.Anno_Param);
                        process.beginAnnotation(a, new Object[] { m, paramType }, stack);
                        process.beginAnnotation(a, new Object[] { m, paramType }, stack);
                        stack = stack.getParent();
                    }
                }
            }
            for (Annotation a : method_anno)
                process.endAnnotation(a, m, stack);
            stack = stack.getParent();
        }
        for (Annotation a : class_anno)
            process.endAnnotation(a, atType, stack);
        return stack;
    }
}