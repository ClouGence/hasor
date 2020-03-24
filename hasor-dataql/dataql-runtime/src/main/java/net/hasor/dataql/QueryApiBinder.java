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
package net.hasor.dataql;
import net.hasor.core.*;
import net.hasor.core.aop.AsmTools;
import net.hasor.core.exts.aop.Matchers;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 全面提供 “数据库 + 服务” 整合查询，并为查询结果提供全面的数据整合能力。您甚至可以通过一条 QL 混合 RPC 和数据库的查询结果。
 * 除此之外，DataQL 软件包，还为您提供全面的数据库事务控制能力，在底层上 DataQL 还为您提供了 DataSource、JDBC操作接口、等常见的功能。
 * 无论您是简单使用数据库查询，还是要整合数据库 + 服务。 DataQL 都将会您最有力的帮手。
 *
 * Data 提供的 DataQL
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface QueryApiBinder extends ApiBinder, Hints {
    /** 加载带有 @DimFragment 注解的类 */
    public default QueryApiBinder loadFragment(Set<Class<?>> fragmentTypeSet) {
        return this.loadFragment(fragmentTypeSet, Matchers.anyClass(), null);
    }

    /** 加载带有 @DimFragment 注解的类 */
    public default QueryApiBinder loadFragment(Set<Class<?>> mabyFragmentTypeSet, Predicate<Class<?>> matcher, TypeSupplier typeSupplier) {
        if (mabyFragmentTypeSet != null && !mabyFragmentTypeSet.isEmpty()) {
            mabyFragmentTypeSet.stream()//
                    .filter(matcher)//
                    .filter(Matchers.annotatedWithClass(DimFragment.class))//
                    .forEach(aClass -> loadFragment(aClass, typeSupplier));
        }
        return this;
    }

    /** 加载带有 @DimFragment 注解的类 */
    public default void loadFragment(Class<?> fragmentType) {
        loadFragment(fragmentType, null);
    }

    /** 加载带有 @DimFragment 注解的类 */
    public default void loadFragment(Class<?> fragmentType, TypeSupplier typeSupplier) {
        Objects.requireNonNull(fragmentType, "class is null.");
        int modifier = fragmentType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || fragmentType.isArray() || fragmentType.isEnum()) {
            throw new IllegalStateException(fragmentType.getName() + " must be normal Bean");
        }
        DimFragment annotationsByType = fragmentType.getAnnotation(DimFragment.class);
        if (annotationsByType == null) {
            throw new IllegalStateException(fragmentType.getName() + " must be configure @DimFragment");
        }
        if (!FragmentProcess.class.isAssignableFrom(fragmentType)) {
            throw new ClassCastException(fragmentType.getName() + " is not " + FragmentProcess.class.getName());
        }
        //
        Supplier<? extends FragmentProcess> processSupplier = null;
        Class<? extends FragmentProcess> processType = (Class<? extends FragmentProcess>) fragmentType;
        if (typeSupplier != null) {
            processSupplier = () -> typeSupplier.get(processType);
        } else {
            processSupplier = getProvider(processType);
        }
        //
        bindFragment(annotationsByType.value(), processSupplier);
    }

    /** 加载带有 @DimUdf 注解的类 */
    public default QueryApiBinder loadUdf(Set<Class<?>> udfTypeSet) {
        return this.loadUdf(udfTypeSet, Matchers.anyClass(), null);
    }

    /** 加载带有 @DimUdf 注解的类 */
    public default QueryApiBinder loadUdf(Set<Class<?>> mabyUdfTypeSet, Predicate<Class<?>> matcher, TypeSupplier typeSupplier) {
        if (mabyUdfTypeSet != null && !mabyUdfTypeSet.isEmpty()) {
            mabyUdfTypeSet.stream()//
                    .filter(matcher)//
                    .filter(Matchers.annotatedWithClass(DimUdf.class))//
                    .forEach(aClass -> loadUdf(aClass, typeSupplier));
        }
        return this;
    }

    /** 加载带有 @DimUdf 注解的类 */
    public default void loadUdf(Class<?> udfType) {
        loadUdf(udfType, null);
    }

    /** 加载带有 @DimUdf 注解的类 */
    public default void loadUdf(Class<?> udfType, TypeSupplier typeSupplier) {
        Objects.requireNonNull(udfType, "class is null.");
        int modifier = udfType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || udfType.isArray() || udfType.isEnum()) {
            throw new IllegalStateException(udfType.getName() + " must be normal Bean");
        }
        DimUdf annotationsByType = udfType.getAnnotation(DimUdf.class);
        if (annotationsByType == null) {
            throw new IllegalStateException(udfType.getName() + " must be configure @DimUdf");
        }
        if (!Udf.class.isAssignableFrom(udfType)) {
            throw new ClassCastException(udfType.getName() + " is not " + Udf.class.getName());
        }
        //
        if (typeSupplier == null) {
            addShareVar(annotationsByType.value(), getProvider(udfType));
        } else {
            addShareVar(annotationsByType.value(), () -> {
                return typeSupplier.get(udfType);
            });
        }
    }

    /** 加载带有 @DimUdfSource 注解的类 */
    public default QueryApiBinder loadUdfSource(Class<?>... udfSourceTypeArrays) {
        return loadUdfSource(new HashSet<>(Arrays.asList(udfSourceTypeArrays)));
    }

    /** 加载带有 @DimUdfSource 注解的类 */
    public default QueryApiBinder loadUdfSource(Set<Class<?>> udfSourceTypeSet) {
        return this.loadUdfSource(udfSourceTypeSet, Matchers.anyClass(), null);
    }

    /** 加载带有 @DimUdfSource 注解的类 */
    public default QueryApiBinder loadUdfSource(Set<Class<?>> mabyUdfTypeSet, Predicate<Class<?>> matcher, TypeSupplier typeSupplier) {
        if (mabyUdfTypeSet != null && !mabyUdfTypeSet.isEmpty()) {
            mabyUdfTypeSet.stream()//
                    .filter(matcher)//
                    .filter(Matchers.annotatedWithClass(DimUdfSource.class))//
                    .forEach(aClass -> loadUdfSource(aClass, typeSupplier));
        }
        return this;
    }

    /** 加载带有 @DimUdfSource 注解的类 */
    public default void loadUdfSource(Class<?> sourceType, TypeSupplier typeSupplier) {
        Objects.requireNonNull(sourceType, "class is null.");
        int modifier = sourceType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || sourceType.isArray() || sourceType.isEnum()) {
            throw new IllegalStateException(sourceType.getName() + " must be normal Bean");
        }
        DimUdfSource annotationsByType = sourceType.getAnnotation(DimUdfSource.class);
        if (annotationsByType == null) {
            throw new IllegalStateException(sourceType.getName() + " must be configure @DimUdfSource");
        }
        if (!UdfSource.class.isAssignableFrom(sourceType)) {
            throw new ClassCastException(sourceType.getName() + " is not " + UdfSource.class.getName());
        }
        //
        Class<? extends UdfSource> udfSourceType = (Class<? extends UdfSource>) sourceType;
        HasorUtils.pushStartListener(getEnvironment(), (EventListener<AppContext>) (event, appContext) -> {
            DataQL dataQL = appContext.getInstance(DataQL.class);
            Finder qlFinder = dataQL.getFinder();
            if (typeSupplier == null) {
                dataQL.addShareVar(annotationsByType.value(), () -> {
                    return appContext.getInstance(udfSourceType).getUdfResource(qlFinder).get();
                });
            } else {
                dataQL.addShareVar(annotationsByType.value(), () -> {
                    return typeSupplier.get(udfSourceType).getUdfResource(qlFinder).get();
                });
            }
        });
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default QueryApiBinder addShareVarInstance(String name, Object instance) {
        return this.addShareVar(name, () -> instance);
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default <T> QueryApiBinder addShareVar(String name, Class<? extends T> implementation) {
        return this.addShareVar(name, () -> {
            return getProvider(implementation).get();
        });
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default <T> QueryApiBinder addShareVar(String name, BindInfo<T> bindInfo) {
        return this.addShareVar(name, () -> {
            return getProvider(bindInfo).get();
        });
    }

    /** 添加全局变量（等同于 compilerVar） */
    public <T> QueryApiBinder addShareVar(String name, Supplier<T> provider);

    public QueryApiBinder bindFinder(Supplier<? extends Finder> finderSupplier);

    /** 注册 FragmentProcess */
    public default QueryApiBinder bindFragment(String fragmentType, FragmentProcess instance) {
        return this.bindFragment(fragmentType, () -> instance);
    }

    /** 注册 FragmentProcess */
    public default <T extends FragmentProcess> QueryApiBinder bindFragment(String fragmentType, Class<? extends T> implementation) {
        return this.bindFragment(fragmentType, getProvider(implementation));
    }

    /** 注册 FragmentProcess */
    public default <T extends FragmentProcess> QueryApiBinder bindFragment(String fragmentType, BindInfo<T> bindInfo) {
        return this.bindFragment(fragmentType, getProvider(bindInfo));
    }

    /** 注册 FragmentProcess */
    public default <T extends FragmentProcess> QueryApiBinder bindFragment(String fragmentType, Supplier<T> provider) {
        bindType(FragmentProcess.class).nameWith(fragmentType).toProvider(provider);
        return this;
    }
}
