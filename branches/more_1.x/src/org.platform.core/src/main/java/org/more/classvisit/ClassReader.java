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
package org.more.classvisit;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * 
 * @version : 2012-4-19
 * @author 赵永春 (zyc@byshell.org)
 */
public final class ClassReader {
    private boolean hasDeclared = true;
    public ClassReader() {
        this(true);
    }
    public ClassReader(boolean hasDeclared) {
        this.hasDeclared = hasDeclared;
    }
    /**扫描注解*/
    public void visitAnnotation(Annotation annoData, AnnotationVisit visit) {
        visit.beginVisit();
        visit.visitAnnotation(annoData);
        visit.endVisit();
    };
    /**扫描类*/
    public void visitClass(Class<?> classData, ClassVisit visit) {
        visit.beginVisit();
        visit.visitInfo(classData, classData.getSuperclass());
        visit.visitFaces(classData.getInterfaces());
        /*注解*/
        Annotation[] annos = (hasDeclared == true) ? classData.getDeclaredAnnotations() : classData.getAnnotations();
        if (annos != null)
            for (Annotation anno : annos) {
                AnnotationVisit annoVisit = visit.visitAnnotation(anno);
                visitAnnotation(anno, annoVisit);
            }
        /*构造方法*/
        Constructor<?>[] cons = (hasDeclared == true) ? classData.getDeclaredConstructors() : classData.getConstructors();
        if (cons != null)
            for (Constructor<?> con : cons) {
                ConstructorVisit conVisit = visit.visitConstructor(con);
                visitConstructor(con, conVisit);
            }
        /*字段*/
        Field[] fields = (hasDeclared == true) ? classData.getDeclaredFields() : classData.getFields();
        if (fields != null)
            for (Field field : fields) {
                FieldVisit fieldVisit = visit.visitField(field);
                visitField(field, fieldVisit);
            }
        /*方法*/
        Method[] methods = (hasDeclared == true) ? classData.getDeclaredMethods() : classData.getMethods();
        if (methods != null)
            for (Method method : methods) {
                MethodVisit methodVisit = visit.visitMethod(method);
                visitMethod(method, methodVisit);
            }
        visit.endVisit();
    };
    /**扫描构造方法*/
    public void visitConstructor(Constructor<?> constructorData, ConstructorVisit visit) {
        visit.beginVisit();
        /*注解*/
        Annotation[] annos = (hasDeclared == true) ? constructorData.getDeclaredAnnotations() : constructorData.getAnnotations();
        if (annos != null)
            for (Annotation anno : annos) {
                AnnotationVisit annoVisit = visit.visitAnnotation(anno);
                visitAnnotation(anno, annoVisit);
            }
        /*参数类型*/
        Class<?>[] paramTypes = constructorData.getParameterTypes();
        Annotation[][] paramAnnos = constructorData.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> $paramType = paramTypes[i];
            Annotation[] $paramAnnos = paramAnnos[i];
            ParamVisit paramVisit = visit.visitParams($paramType, $paramAnnos);
            paramVisit.beginVisit();
            for (Annotation anno : $paramAnnos) {
                AnnotationVisit annoVisit = paramVisit.visitAnnotation(anno);
                visitAnnotation(anno, annoVisit);
            }
            paramVisit.endVisit();
        }
        /*异常*/
        visit.visitThrows(constructorData.getExceptionTypes());
        visit.endVisit();
    };
    /**扫描字段*/
    public void visitField(Field fieldData, FieldVisit visit) {
        visit.beginVisit();
        /*注解*/
        Annotation[] annos = (hasDeclared == true) ? fieldData.getDeclaredAnnotations() : fieldData.getAnnotations();
        if (annos != null)
            for (Annotation anno : annos) {
                AnnotationVisit annoVisit = visit.visitAnnotation(anno);
                visitAnnotation(anno, annoVisit);
            }
        visit.endVisit();
    };
    /**扫描方法*/
    public void visitMethod(Method methodData, MethodVisit visit) {
        visit.beginVisit();
        /*注解*/
        Annotation[] annos = (hasDeclared == true) ? methodData.getDeclaredAnnotations() : methodData.getAnnotations();
        if (annos != null)
            for (Annotation anno : annos) {
                AnnotationVisit annoVisit = visit.visitAnnotation(anno);
                visitAnnotation(anno, annoVisit);
            }
        /*参数类型*/
        Class<?>[] paramTypes = methodData.getParameterTypes();
        Annotation[][] paramAnnos = methodData.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> $paramType = paramTypes[i];
            Annotation[] $paramAnnos = paramAnnos[i];
            ParamVisit paramVisit = visit.visitParams($paramType, $paramAnnos);
            paramVisit.beginVisit();
            for (Annotation anno : $paramAnnos) {
                AnnotationVisit annoVisit = paramVisit.visitAnnotation(anno);
                visitAnnotation(anno, annoVisit);
            }
            paramVisit.endVisit();
        }
        /*异常*/
        visit.visitThrows(methodData.getExceptionTypes());
        /*返回*/
        visit.visitReturnType(methodData.getReturnType(), methodData.getDefaultValue());
        visit.endVisit();
    };
}