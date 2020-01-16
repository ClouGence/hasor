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
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.aop.AsmTools;
import net.hasor.core.exts.aop.Matchers;

import java.lang.reflect.Modifier;
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
    /** 加载带有 @DimFragment 注解的类。 */
    public default QueryApiBinder loadFragment(Set<Class<?>> fragmentTypeSet) {
        return this.loadFragment(fragmentTypeSet, Matchers.annotatedWithClass(DimFragment.class));
    }

    /** 加载带有 @DimFragment 注解的类。 */
    public default QueryApiBinder loadFragment(Set<Class<?>> mabeFragmentTypeSet, Predicate<Class<?>> matcher) {
        if (mabeFragmentTypeSet != null && !mabeFragmentTypeSet.isEmpty()) {
            mabeFragmentTypeSet.stream().filter(matcher).forEach(this::loadFragment);
        }
        return this;
    }

    /** 加载带有 @DimFragment 注解的类。 */
    public default void loadFragment(Class<?> fragmentType) {
        Objects.requireNonNull(fragmentType, "class is null.");
        int modifier = fragmentType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || fragmentType.isArray() || fragmentType.isEnum()) {
            throw new IllegalStateException(fragmentType.getName() + " must be normal Bean");
        }
        DimFragment annotationsByType = fragmentType.getAnnotation(DimFragment.class);
        if (annotationsByType == null) {
            throw new IllegalStateException(fragmentType.getName() + " must be configure @DimFragment");
        }
        //
        if (FragmentProcess.class.isAssignableFrom(fragmentType)) {
            addShareVar(annotationsByType.value(), getProvider(fragmentType));
        }
    }

    /** 加载带有 @DimUdf 注解的类。 */
    public default QueryApiBinder loadUdf(Set<Class<?>> udfTypeSet) {
        return this.loadUdf(udfTypeSet, Matchers.annotatedWithClass(DimUdf.class));
    }

    /** 加载带有 @DimUdf 注解的类。 */
    public default QueryApiBinder loadUdf(Set<Class<?>> mabeUdfTypeSet, Predicate<Class<?>> matcher) {
        if (mabeUdfTypeSet != null && !mabeUdfTypeSet.isEmpty()) {
            mabeUdfTypeSet.stream().filter(matcher).forEach(this::loadUdf);
        }
        return this;
    }

    /** 加载带有 @DimUdf 注解的类。 */
    public default void loadUdf(Class<?> udfType) {
        Objects.requireNonNull(udfType, "class is null.");
        int modifier = udfType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || udfType.isArray() || udfType.isEnum()) {
            throw new IllegalStateException(udfType.getName() + " must be normal Bean");
        }
        DimUdf annotationsByType = udfType.getAnnotation(DimUdf.class);
        if (annotationsByType == null) {
            throw new IllegalStateException(udfType.getName() + " must be configure @DimUdf");
        }
        //
        if (Udf.class.isAssignableFrom(udfType)) {
            addShareVar(annotationsByType.value(), getProvider(udfType));
        }
    }

    /** 加载带有 @DimUdfSource 注解的类。 */
    public default QueryApiBinder loadUdfSource(Set<Class<?>> udfTypeSet) {
        return this.loadUdfSource(udfTypeSet, Matchers.annotatedWithClass(DimUdfSource.class));
    }

    /** 加载带有 @DimUdfSource 注解的类。 */
    public default QueryApiBinder loadUdfSource(Set<Class<?>> mabeUdfTypeSet, Predicate<Class<?>> matcher) {
        if (mabeUdfTypeSet != null && !mabeUdfTypeSet.isEmpty()) {
            mabeUdfTypeSet.stream().filter(matcher).forEach(this::loadUdfSource);
        }
        return this;
    }

    /** 加载带有 @DimUdfSource 注解的类。 */
    public default void loadUdfSource(Class<?> udfType) {
        Objects.requireNonNull(udfType, "class is null.");
        int modifier = udfType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || udfType.isArray() || udfType.isEnum()) {
            throw new IllegalStateException(udfType.getName() + " must be normal Bean");
        }
        DimUdfSource annotationsByType = udfType.getAnnotation(DimUdfSource.class);
        if (annotationsByType == null) {
            throw new IllegalStateException(udfType.getName() + " must be configure @DimUdfSource");
        }
        //
        if (UdfSource.class.isAssignableFrom(udfType)) {
            addShareVar(annotationsByType.value(), getProvider(udfType));
        }
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default QueryApiBinder addShareVarInstance(String name, Object instance) {
        return this.addShareVar(name, () -> instance);
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default <T> QueryApiBinder addShareVar(String name, Class<? extends T> implementation) {
        return this.addShareVar(name, getProvider(implementation));
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default <T> QueryApiBinder addShareVar(String name, BindInfo<T> bindInfo) {
        return this.addShareVar(name, getProvider(bindInfo));
    }

    /** 添加全局变量（等同于 compilerVar） */
    public <T> QueryApiBinder addShareVar(String name, Supplier<T> provider);

    public QueryApiBinder bindFinder(Finder finder);

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