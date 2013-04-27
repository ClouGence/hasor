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
package org.guice;
import java.util.List;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
public class GuiceTest {
    /** */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Injector guice = Guice.createInjector(new GuiceModule());
        Bean1 bean1 = guice.getInstance(Bean1.class);
        bean1.print();
        System.out.println();
        List<Binding<Faces>> faceBind = guice.findBindingsByType(TypeLiteral.get(Faces.class));
        for (Binding<Faces> face : faceBind)
            face.getProvider().get().pring();
        Faces faces = guice.getInstance(Faces.class);
        System.out.println(faces.getClass());
        AAAA a = guice.getInstance(Key.get(AAAA.class));
        System.out.println(a);
    }
}
class AAAA {}
//@SuppressWarnings({ "unchecked", "rawtypes" })
//class BeanProvider extends TypeLiteral {
//    @Override
//    public TypeLiteral getSupertype(Class supertype) {
//        // TODO Auto-generated method stub
//        return super.getSupertype(supertype);
//    }
//    @Override
//    public TypeLiteral getFieldType(Field field) {
//        // TODO Auto-generated method stub
//        return super.getFieldType(field);
//    }
//    @Override
//    public List getParameterTypes(Member methodOrConstructor) {
//        // TODO Auto-generated method stub
//        return super.getParameterTypes(methodOrConstructor);
//    }
//    @Override
//    public List getExceptionTypes(Member methodOrConstructor) {
//        // TODO Auto-generated method stub
//        return super.getExceptionTypes(methodOrConstructor);
//    }
//    @Override
//    public TypeLiteral getReturnType(Method method) {
//        // TODO Auto-generated method stub
//        return super.getReturnType(method);
//    }
//}  