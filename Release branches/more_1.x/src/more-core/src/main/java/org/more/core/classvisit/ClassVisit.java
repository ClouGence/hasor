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
package org.more.core.classvisit;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * 
 * @version : 2012-4-19
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ClassVisit {
    /**开始拜访*/
    public void beginVisit();
    /**名字信息*/
    public void visitInfo(Class<?> tagetClass, Class<?> superClass);
    /**名字信息*/
    public void visitFaces(Class<?>[] faces);
    /**注解Visit*/
    public AnnotationVisit visitAnnotation(Annotation annotation);
    /**构造方法Visit*/
    public ConstructorVisit visitConstructor(Constructor<?> constructor);
    /**获取字段方法Visit*/
    public FieldVisit visitField(Field field);
    /**获取方法Visit*/
    public MethodVisit visitMethod(Method method);
    /**结束拜访*/
    public void endVisit();
}